package ru.maxthetomas.e418.neoforge.player;

import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.neoforge.AttachmentTypes;
import ru.maxthetomas.e418.player.IPlayerDataManager;
import ru.maxthetomas.e418.player.PlayerData;

public class NeoForgePlayerDataManager implements IPlayerDataManager {

    @Override
    public PlayerData ensureData(ServerPlayer player) {
        if (!player.hasData(AttachmentTypes.PLAYER_DATA)) {
            var data = createNewData(player.server);
            player.setData(AttachmentTypes.PLAYER_DATA, data);
            return data;
        }


        return player.getData(AttachmentTypes.PLAYER_DATA).duplicate();
    }

    @Override
    public PlayerData getData(ServerPlayer player) {
        if (!player.hasData(AttachmentTypes.PLAYER_DATA))
            return null;

        return player.getData(AttachmentTypes.PLAYER_DATA).duplicate();
    }

    @Override
    public void setData(ServerPlayer player, PlayerData data) {
        player.setData(AttachmentTypes.PLAYER_DATA, data);
    }
}
