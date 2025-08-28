package ru.maxthetomas.e418.fabric.player;

import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.fabric.AttachmentTypes;
import ru.maxthetomas.e418.player.IPlayerDataManager;
import ru.maxthetomas.e418.player.PlayerData;

public class FabricPlayerDataManager implements IPlayerDataManager {
    @Override
    public PlayerData ensureData(ServerPlayer player) {
        var attachment = player.getAttachedOrCreate(AttachmentTypes.PLAYER_DATA_ATTACHMENT,
                () -> new PlayerDataAttachment(createNewData(player.server))
        );
        return attachment.playerData().duplicate();
    }

    @Override
    public PlayerData getData(ServerPlayer player) {
        var attachment = player.getAttached(AttachmentTypes.PLAYER_DATA_ATTACHMENT);
        if (attachment == null)
            return null;
        else
            return attachment.playerData().duplicate();
    }

    @Override
    public void setData(ServerPlayer player, PlayerData data) {
        player.setAttached(AttachmentTypes.PLAYER_DATA_ATTACHMENT, new PlayerDataAttachment(data));
    }

    @Override
    public void reset() {

    }
}
