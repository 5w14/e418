package ru.maxthetomas.e418.fabric;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.fabric.player.PlayerDataAttachment;

public class AttachmentTypes {
    public static final AttachmentType<PlayerDataAttachment> PLAYER_DATA_ATTACHMENT = AttachmentRegistry.create(
            E418.resLoc("player_data"),
            builder -> builder
                    .initializer(() -> PlayerDataAttachment.DEFAULT)
                    .persistent(PlayerDataAttachment.CODEC)
                    .copyOnDeath()
    );
}
