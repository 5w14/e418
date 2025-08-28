package ru.maxthetomas.e418.player;

import net.minecraft.server.MinecraftServer;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.util.E418Random;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {
    private static final HashMap<UUID, PlayerData> players = new HashMap<>();

    /**
     * Returns data of a player. If it doesn't exist, it creates one and then returns it.
     *
     * @param uuid UUID of player
     * @param srv  Server
     * @return Player data
     */
    public static PlayerData ensureData(UUID uuid, MinecraftServer srv) {
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else {
            var newData = createNewData(srv);
            players.put(uuid, newData);
            return newData;
        }
    }

    /**
     * Returns data of a player. If it doesn't exist, it returns null.
     *
     * @param uuid UUID of player
     * @return Player data
     */
    public static PlayerData getData(UUID uuid) {
        return players.get(uuid);
    }

    /**
     * @return Returns UUIDs of saved players
     */
    public static Collection<UUID> savedPlayers() {
        return players.keySet();
    }

    /**
     * Returns new player data.
     *
     * @param srv Server
     * @return Player data
     */
    public static PlayerData createNewData(MinecraftServer srv) {
        var data = new PlayerData();

        data.eventTimestamp = srv.overworld().getGameTime() + Config.playerRandomEventGracePeriod.get().randomValue(E418Random.EVENT_ENGINE_PLAYER);

        return data;
    }

    public static void reset() {
        players.clear();
    }
}
