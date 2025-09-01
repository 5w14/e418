package ru.maxthetomas.e418.player;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;

/**
 * Simplified player data manager that doesn't save between sessions.
 */
public class SimplifiedDataManager implements IPlayerDataManager {
    private static final HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public PlayerData ensureData(ServerPlayer player) {
        var uuid = player.getUUID();
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else {
            var newData = createNewData(player.server);
            players.put(uuid, newData);
            return newData;
        }
    }

    @Override
    public PlayerData getData(ServerPlayer player) {
        return players.get(player.getUUID());
    }

    @Override
    public void setData(ServerPlayer player, PlayerData data) {
        players.put(player.getUUID(), data);
    }

    @Override
    public void reset() {
        players.clear();
    }
}
