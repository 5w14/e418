package ru.maxthetomas.e418.neoforge.storage;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.util.storage.data.ChunkData;
import ru.maxthetomas.e418.util.storage.data.PlayerData;

import java.util.function.Supplier;

public class AttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, E418.MOD_ID);

    public static final Supplier<AttachmentType<PlayerData>> PLAYER_DATA = ATTACHMENT_TYPES.register(
            "player_data", () -> AttachmentType.builder(PlayerData::new).serialize(PlayerData.CODEC).copyOnDeath().build());

    public static final Supplier<AttachmentType<ChunkData>> CHUNK_DATA = ATTACHMENT_TYPES.register(
            "chunk_data", () -> AttachmentType.builder(ChunkData::new).serialize(ChunkData.CODEC).copyOnDeath().build());
}
