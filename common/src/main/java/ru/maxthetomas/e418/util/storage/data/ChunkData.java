package ru.maxthetomas.e418.util.storage.data;

import com.mojang.serialization.Codec;

public class ChunkData implements IData<ChunkData> {
    public static final Codec<ChunkData> CODEC = Codec.unit(new ChunkData());

    public ChunkData duplicate() {
        return new ChunkData();
    }
}

