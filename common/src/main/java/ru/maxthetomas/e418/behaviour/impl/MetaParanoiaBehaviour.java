package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
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
import ru.maxthetomas.e418.util.E418Variables;

/// Makes that you won't be able to leave
public class MetaParanoiaBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "meta_paranoia");
    public static final MapCodec<MetaParanoiaBehaviour> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(MetaParanoiaBehaviour::new));

    public MetaParanoiaBehaviour() {
        PlayerEvent.PLAYER_JOIN.register(this::playerJoin);
    }
    
    void playerJoin(ServerPlayer player) {
        if (isExecuted() && !isDone())
            NetworkManager.sendToPlayer(player, new S2CSetMetaParanoia(true));
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
    }
    
    private void setMetaParanoia(boolean value) {
        // Send to all players
        NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CSetMetaParanoia(value));
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
    
    @Override
    public boolean restoreState(EventContext context, IBehaviourExecutor executor) {
        if (isExecuted() && !isDone()) {
            _resetExecuted();
        }
        
        return super.restoreState(context, executor);
    }

    @Override
    public boolean canRun(EventContext context) {
        return !context.shouldAwaitPlayer();
    }
}
