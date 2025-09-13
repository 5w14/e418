package ru.maxthetomas.e418.system;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import ru.maxthetomas.e418.util.Location;

import java.util.HashMap;

public class TemporalShiftSystem {
    private static HashMap<String, Location> inShift = new HashMap<>();

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register((player) -> {
            if (inShift.containsKey(player.getUUID().toString())) {
                var location = inShift.get(player.getUUID().toString());

                player.teleport(new TeleportTransition(
                        location.level(),
                        location.position(),
                        Vec3.ZERO,
                        player.getYRot(),
                        player.getXRot(),
                        TeleportTransition.DO_NOTHING));

                TemporalShiftSystem.removeShift(player.getUUID().toString());
            }
        });
    }

    public static void setPlayersInShift(HashMap<String, Location> inShift) {
        TemporalShiftSystem.inShift = inShift;
    }

    public static HashMap<String, Location> getPlayersInShift() {
        return inShift;
    }

    public static void addShift(String id, Location location) {
        inShift.put(id, location);
    }

    public static void removeShift(String id) {
        inShift.remove(id);
    }
}
