package ru.maxthetomas.e418.util.storage.data;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.chunk.ChunkAccess;
import ru.maxthetomas.e418.util.storage.PlatformDataManager;

public class ChunkData implements IData<ChunkData> {
    public static final Codec<ChunkData> CODEC = Codec.unit(new ChunkData());

    public ChunkData duplicate() {
        return new ChunkData();
    }

    public static ChunkData ensureData(ChunkAccess chunk) {
        return PlatformDataManager.ensureData(PlatformDataManager.CHUNK_DATA, chunk, ChunkData::new);
    }
}

