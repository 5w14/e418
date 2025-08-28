package ru.maxthetomas.e418.event.engine;

import net.minecraft.server.MinecraftServer;

public class EventEngine {
    public final RandomEventManager RandomEventManager = new RandomEventManager();
    public final ChatMessageEventManager ChatMessageEventManager = new ChatMessageEventManager();

    public void reset(MinecraftServer srv) {
        RandomEventManager.reset(srv);
        ChatMessageEventManager.reset();
    }
}
