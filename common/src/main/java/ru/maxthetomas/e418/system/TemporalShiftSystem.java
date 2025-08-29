package ru.maxthetomas.e418.system;

import dev.architectury.event.events.common.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

public class TemporalShiftSystem {
    private static HashMap<String, Boolean> inShift = new HashMap<>();

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register((player) -> {
            if (inShift.containsKey(player.getUUID().toString())) {
                inShift.replace(player.getUUID().toString(), false);
            }
        });
        PlayerEvent.PLAYER_QUIT.register((player) -> {
            if (inShift.containsKey(player.getUUID().toString())) {
                inShift.replace(player.getUUID().toString(), true);
            }
        });
    }

    public static void setPlayersInShift(HashMap<String, Boolean> inShift) {
        TemporalShiftSystem.inShift = inShift;
    }

    public static HashMap<String, Boolean> getPlayersInShift() {
        return inShift;
    }

    public static void addShift(UUID id, boolean paused) {
        inShift.put(id.toString(), paused);
    }

    public static boolean isPaused(UUID id) {
        return inShift.get(id.toString());
    }
}
