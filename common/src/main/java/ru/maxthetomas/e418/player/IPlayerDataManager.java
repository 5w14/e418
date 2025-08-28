package ru.maxthetomas.e418.player;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import ru.maxthetomas.e418.config.Config;

public interface IPlayerDataManager {
    /**
     * Returns data of a player. If it doesn't exist, it creates one and then returns it.
     *
     * @param player Player
     * @return Player data
     */
    PlayerData ensureData(ServerPlayer player);

    /**
     * Returns data of a player. If it doesn't exist, it returns null.
     *
     * @param player Player
     * @return Player data
     */
    PlayerData getData(ServerPlayer player);

    /**
     * Set player data to player
     *
     * @param player Player
     * @param data   Data
     */
    void setData(ServerPlayer player, PlayerData data);

    default void reset() {
    }

    /**
     * Returns new player data.
     *
     * @param srv Server
     * @return Player data
     */
    default PlayerData createNewData(MinecraftServer srv) {

        var data = new PlayerData();
        data.eventTimestamp = srv.overworld().getGameTime() + Config.playerRandomEventGracePeriod.get().randomValue(RandomSource.create());
        return data;

    }
}
