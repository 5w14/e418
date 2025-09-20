package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.codecs.FogCodecs;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.networking.S2CSetFog;

/**
 * Overrides every biome to fog. Use stop() to end.
 */
public class FogOverrideBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("fog_override");
    public static final MapCodec<FogOverrideBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FogCodecs.CODEC.fieldOf("fog").forGetter(b -> b.config)
    ).apply(instance, FogOverrideBehaviour::new));
    public static final MapCodec<FogOverrideBehaviour> STATE_CODEC = CODEC;

    final FogCodecs.FogConfig config;

    public FogOverrideBehaviour(FogCodecs.FogConfig config) {
        this.config = config;
        PlayerEvent.PLAYER_JOIN.register(this::playerJoin);
    }

    void playerJoin(ServerPlayer player) {
        if (isExecuted() && !isDone()) {
            if (context.hasPlayer() && !player.getUUID().equals(context.getPlayerUUID()))
                return;

            NetworkManager.sendToPlayer(player, new S2CSetFog(true, config));
        }
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        this.apply(new S2CSetFog(true, config));
    }

    void apply(S2CSetFog packet) {
        if (context.hasPlayer()) {
            if (context.getPlayer() == null)
                return;

            NetworkManager.sendToPlayer(context.getPlayer(), packet);
            return;
        }

        NetworkManager.sendToPlayers(E418.allPlayers(), packet);
    }

    @Override
    public void restoreState(EventContext context, IBehaviourExecutor executor) {
        super.restoreState(context, executor);
        if (this.isExecuted() && !isDone()) {
            apply(new S2CSetFog(true, config));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        PlayerEvent.PLAYER_JOIN.unregister(this::playerJoin);
        this.apply(new S2CSetFog(false, FogCodecs.FogConfig.EMPTY));
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
