package ru.maxthetomas.e418.player;

import net.minecraft.server.MinecraftServer;

public class PlayerData {
    /**
     * Time in ticks when event bound to player will start.
     */
    public long eventTimestamp = -1;

    public PlayerData(MinecraftServer srv) {
        // TODO: Add delay from config
        eventTimestamp = srv.overworld().getGameTime() + 1200L;
    }
}
