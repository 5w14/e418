package ru.maxthetomas.votvevents.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public class EventContext {
    private final MinecraftServer server;
    private Player player;
    private ActiveEvent sourceEvent;

    // TODO add more fields here

    public EventContext(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ActiveEvent getSourceEvent() {
        return sourceEvent;
    }

    public Player getPlayer() {
        return player;
    }

    public EventContext withSourceEvent(ActiveEvent sourceEvent) {
        this.sourceEvent = sourceEvent;
        return this;
    }

    public EventContext withPlayer(Player player) {
        this.player = player;
        return this;
    }

    @Override
    public EventContext clone() {
        var newContext = new EventContext(server);

        newContext.player = player;
        // TODO add more fields here

        return newContext;
    }
}
