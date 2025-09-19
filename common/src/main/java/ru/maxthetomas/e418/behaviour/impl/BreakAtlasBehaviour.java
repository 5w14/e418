package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.networking.S2CSetBreakAtlas;

/**
 * Breaks game's texture atlas to make textures look glitchy
 */
public class BreakAtlasBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("break_atlas");
    public static final MapCodec<BreakAtlasBehaviour> CODEC = MapCodec.unit(BreakAtlasBehaviour::new);
    public static final MapCodec<BreakAtlasBehaviour> STATE_CODEC = MapCodec.unit(BreakAtlasBehaviour::new);

    public BreakAtlasBehaviour() {
        register();
    }

    void register() {
        PlayerEvent.PLAYER_JOIN.register(this::onPlayerJoin);
    }

    void unregister() {
        PlayerEvent.PLAYER_JOIN.unregister(this::onPlayerJoin);
    }

    void onPlayerJoin(ServerPlayer player) {
        if (isExecuted() && !isDone() && (!context.hasPlayer() || context.getPlayerUUID().equals(player.getUUID()))) {
            setBreakAtlas(player, true);
        }
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setBreakAtlas(context.getPlayer(), true);
    }

    @Override
    public void dispose() {
        super.dispose();
        unregister();
        setBreakAtlas(context.getPlayer(), false);
    }

    private void setBreakAtlas(ServerPlayer player, boolean value) {
        if (player == null) {
            NetworkManager.sendToPlayers(E418.allPlayers(), new S2CSetBreakAtlas(value));
        } else {
            NetworkManager.sendToPlayer(player, new S2CSetBreakAtlas(value));
        }
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
