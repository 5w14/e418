package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

public class ExecuteCommandBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "execute_command");
    public static final MapCodec<ExecuteCommandBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("command").forGetter(ExecuteCommandBehaviour::getCommand),
            Codec.BOOL.optionalFieldOf("as_player", false).forGetter(ExecuteCommandBehaviour::isAsPlayer)
    ).apply(instance, ExecuteCommandBehaviour::new));

    private final String command;
    private final boolean asPlayer;

    public ExecuteCommandBehaviour(String command, boolean asPlayer) {
        this.command = command;
        this.asPlayer = asPlayer;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    public String getCommand() {
        return command;
    }

    public boolean isAsPlayer() {
        return asPlayer;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        CommandSourceStack stack;

        if (asPlayer && context.getPlayer() == null)
            return;

        if (asPlayer) {
            stack = context.getPlayer().createCommandSourceStack();
        } else {
            stack = context.getServer().createCommandSourceStack();
        }

        // Datapack-level permissions.
        stack = stack.withPermission(2);

        // Remove command output for operators, to not spoil ongoing events.
        if (!Config.isDebug())
            stack = stack.withSuppressedOutput();

        if (context.getLocation() != null) {
            stack = stack
                    .withLevel(context.getLocation().level())
                    .withPosition(context.getLocation().position());
        }

        context.getServer().getCommands().performPrefixedCommand(stack, this.command);
    }

    @Override
    public boolean canRun(EventContext context) {
        return !asPlayer || context.getPlayer() != null;
    }
}
