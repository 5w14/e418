package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class ExecuteCommandBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "execute_command");
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
    public void execute(EventContext context) {
        super.execute(context);
        setDone(true);

        CommandSourceStack stack;

        if (asPlayer && context.getPlayer() == null)
            return;

        if (asPlayer) {
            stack = ((ServerPlayer) context.getPlayer()).createCommandSourceStack();
        } else {
            stack = context.getServer().createCommandSourceStack();
        }

        stack = stack.withPermission(2);

        if (context.getLocation() != null) {
            stack = stack
                    .withLevel(context.getLocation().getLevel())
                    .withPosition(context.getLocation().getPosition());
        }

        context.getServer().getCommands().performPrefixedCommand(stack, this.command);
    }

    @Override
    public boolean canRun(EventContext context) {
        return !asPlayer || context.getPlayer() != null;
    }
}
