package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;
import ru.maxthetomas.votvevents.networking.S2CSetShader;

public class SetShaderBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "set_shader");
    public static final MapCodec<SetShaderBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("shader", ResourceLocation.withDefaultNamespace("empty"))
                    .forGetter(SetShaderBehaviour::getShaderId)
    ).apply(instance, SetShaderBehaviour::new));

    private final ResourceLocation shaderId;
    private ServerPlayer player = null;

    public SetShaderBehaviour(ResourceLocation shaderId) {
        this.shaderId = shaderId;
    }

    public ResourceLocation getShaderId() {
        return shaderId;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);

        var player = context.getPlayer();
        if (player == null) return;
        NetworkManager.sendToPlayer(player, new S2CSetShader(shaderId));
        this.player = player;
    }

    @Override
    public void stop() {
        super.stop();
        NetworkManager.sendToPlayer(player, new S2CSetShader(S2CSetShader.EMPTY_SHADER));
        setDone(true);
    }

    @Override
    public boolean canRun(EventContext context) {
        return context.getPlayer() != null;
    }
}
