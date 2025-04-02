package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.util.exceptions.IntegerPointerException;

public class GameCrashBehaviour implements IBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "game_crash");
    public static final MapCodec<GameCrashBehaviour> CODEC = MapCodec.unit(GameCrashBehaviour::new);

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context) {
        IntegerPointerException.youJustLostTheGame();
    }
}
