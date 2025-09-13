package ru.maxthetomas.e418.system;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import ru.maxthetomas.e418.util.Location;

import java.util.HashMap;
import java.util.UUID;

public class TemporalShiftSystem {
    private static HashMap<UUID, Location> inShift = new HashMap<>();

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register((player) -> {
            if (inShift.containsKey(player.getUUID())) {
                var location = inShift.get(player.getUUID());

                player.teleport(new TeleportTransition(
                        location.level(),
                        location.position(),
                        Vec3.ZERO,
                        player.getYRot(),
                        player.getXRot(),
                        TeleportTransition.DO_NOTHING));

                TemporalShiftSystem.removeShift(player.getUUID());
            }
        });
    }

    public static void setPlayersInShift(HashMap<UUID, Location> inShift) {
        TemporalShiftSystem.inShift = inShift;
    }

    public static HashMap<UUID, Location> getPlayersInShift() {
        return inShift;
    }

    public static void addShift(UUID id, Location location) {
        inShift.put(id, location);
    }

    public static void removeShift(UUID id) {
        inShift.remove(id);
    }
}
