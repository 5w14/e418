package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.networking.S2CCrashGame;

public class GameCrashBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "game_crash");
    public static final MapCodec<GameCrashBehaviour> CODEC = MapCodec.unit(GameCrashBehaviour::new);

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CCrashGame());
        setDone(true);
    }

    @Override
    public boolean canRun(EventContext context) {
        // todo check if intrusive events are enabled
        return super.canRun(context);
    }
}
