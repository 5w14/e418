package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.MapCodec;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;
import ru.maxthetomas.votvevents.networking.S2CCrashGame;

public class GameCrashBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "game_crash");
    public static final MapCodec<GameCrashBehaviour> CODEC = MapCodec.unit(GameCrashBehaviour::new);

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        NetworkManager.sendToPlayers(VotvEvents.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CCrashGame());
        setDone(true);
    }

    @Override
    public boolean canRun(EventContext context) {
        // todo check if intrusive events are enabled
        return super.canRun(context);
    }
}
