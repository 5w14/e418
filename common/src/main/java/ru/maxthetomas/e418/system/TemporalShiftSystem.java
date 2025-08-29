package ru.maxthetomas.e418.system;

import dev.architectury.event.events.common.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

public class TemporalShiftSystem {
    private static HashMap<UUID, Boolean> inShift = new HashMap<>();

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register((player) -> {
            if (inShift.containsKey(player.getUUID())) {
                inShift.replace(player.getUUID(), false);
            }
        });
        PlayerEvent.PLAYER_QUIT.register((player) -> {
            if (inShift.containsKey(player.getUUID())) {
                inShift.replace(player.getUUID(), true);
            }
        });
    }

    public static void addShift(UUID id, boolean paused) {
        inShift.put(id, paused);
    }

    public static boolean isPaused(UUID id) {
        return inShift.get(id);
    }
}
