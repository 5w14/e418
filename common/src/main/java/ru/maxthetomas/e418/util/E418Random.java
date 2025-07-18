package ru.maxthetomas.e418.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import ru.maxthetomas.e418.E418;

public class E418Random {
    public static RandomSource EVENT_ENGINE_GLOBAL;
    public static RandomSource EVENT_ENGINE_PLAYER;
    public static RandomSource EVENT_ENGINE_WAKE_UP;
    public static RandomSource EVENT_ENGINE_CHAT;
    public static RandomSource EVENT_GENERIC;

    public static void init(MinecraftServer server) {
        var level = server.overworld();

        EVENT_ENGINE_GLOBAL = level.getRandomSequence(E418.resLoc("event_engine/random_global"));
        EVENT_ENGINE_PLAYER = level.getRandomSequence(E418.resLoc("event_engine/random_player"));
        EVENT_ENGINE_WAKE_UP = level.getRandomSequence(E418.resLoc("event_engine/wake_up"));
        EVENT_ENGINE_CHAT = level.getRandomSequence(E418.resLoc("event_engine/chat"));
        EVENT_GENERIC = level.getRandomSequence(E418.resLoc("event/generic"));
    }


}
