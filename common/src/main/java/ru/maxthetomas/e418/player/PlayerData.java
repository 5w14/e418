package ru.maxthetomas.e418.player;

public class PlayerData {
    /**
     * Time in ticks when event bound to player will start.
     */
    public long eventTimestamp = -1;

    /**
     * Time in ticks where events for this player are unlocked.
     * <p>
     * When events for player are locked, attempts to trigger player event with this them in range will be cancelled.
     */
    public long eventUnlockTimestamp = 0;
}
