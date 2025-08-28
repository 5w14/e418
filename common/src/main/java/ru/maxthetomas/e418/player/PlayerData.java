package ru.maxthetomas.e418.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerData {
    public static Codec<PlayerData> CODEC = RecordCodecBuilder.<PlayerData>create(instance -> instance.group(
            Codec.LONG.optionalFieldOf("event_timestamp", -1L).forGetter(v -> v.eventTimestamp),
            Codec.LONG.optionalFieldOf("event_unlock_timestamp", 0L).forGetter(v -> v.eventUnlockTimestamp)
    ).apply(instance, PlayerData::constructFromCodec));

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

    public static PlayerData constructFromCodec(long eventTimestamp, long eventUnlockTimestamp) {
        var data = new PlayerData();

        data.eventTimestamp = eventTimestamp;
        data.eventUnlockTimestamp = eventUnlockTimestamp;

        return data;
    }

    public PlayerData duplicate() {
        var data = new PlayerData();

        data.eventTimestamp = this.eventTimestamp;
        data.eventUnlockTimestamp = this.eventUnlockTimestamp;

        return data;
    }
}
