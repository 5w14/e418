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
import ru.maxthetomas.e418.networking.S2CSetMetaParanoia;

/**
 * Prevents the user from being able to leave.
 */
public class MetaParanoiaBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("meta_paranoia");
    public static final MapCodec<MetaParanoiaBehaviour> CODEC = MapCodec.unit(MetaParanoiaBehaviour::new);
    public static final MapCodec<MetaParanoiaBehaviour> STATE_CODEC = MapCodec.unit(MetaParanoiaBehaviour::new);

    public MetaParanoiaBehaviour() {
        PlayerEvent.PLAYER_JOIN.register(this::playerJoin);
    }

    void playerJoin(ServerPlayer player) {
        if (isExecuted() && !isDone() && context.hasPlayer() && context.getPlayer() != null)
            player.server.execute(() -> {
                NetworkManager.sendToPlayer(context.getPlayer(), new S2CSetMetaParanoia(true));
            });
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setMetaParanoia(true);

        // TODO: remove game's ability to save while this event is happening
        // why? -max
    }

    private void setMetaParanoia(boolean value) {
        // Send to all players
        NetworkManager.sendToPlayers(E418.allPlayers(), new S2CSetMetaParanoia(value));
    }

    @Override
    public void stop() {
        setMetaParanoia(false);
        setDone(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        PlayerEvent.PLAYER_JOIN.unregister(this::playerJoin);
        setMetaParanoia(false);
    }
}
