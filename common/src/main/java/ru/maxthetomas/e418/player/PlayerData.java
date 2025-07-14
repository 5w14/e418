package ru.maxthetomas.e418.player;

import net.minecraft.server.MinecraftServer;

public class PlayerData {
    /**
     * Time in ticks when event bound to player will start.
     */
    public long eventTimestamp = -1;

    /**
     * Time in ticks where events for this player are unlocked.
     *
     * When events for player are locked, attempts to trigger player event with this them in range will be cancelled.
     */
    public long eventUnlockTimestamp = 0;

    public PlayerData(MinecraftServer srv) {
        // TODO: Add delay from config
        eventTimestamp = srv.overworld().getGameTime() + 1200L;
    }
}
