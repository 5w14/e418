package ru.maxthetomas.e418.fabric.storage;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.util.storage.data.ChunkData;
import ru.maxthetomas.e418.util.storage.data.PlayerData;

@SuppressWarnings("UnstableApiUsage")
public class AttachmentTypes {
    public static final AttachmentType<PlayerData> PLAYER_DATA = AttachmentRegistry.create(
            E418.resLoc("player_data"),
            builder -> builder
                    .initializer(PlayerData::new)
                    .persistent(PlayerData.CODEC)
                    .copyOnDeath()
    );

    public static final AttachmentType<ChunkData> CHUNK_DATA = AttachmentRegistry.create(
            E418.resLoc("chunk_data"),
            builder -> builder
                    .initializer(ChunkData::new)
                    .persistent(ChunkData.CODEC)
                    .copyOnDeath()
    );
}
