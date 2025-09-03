package ru.maxthetomas.e418.util.storage;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import ru.maxthetomas.e418.util.storage.data.ChunkData;
import ru.maxthetomas.e418.util.storage.data.PlayerData;

import java.util.function.Supplier;

public class PlatformDataManager {
    public static PlatformDataType<ServerPlayer, PlayerData> PLAYER_DATA = PlatformDataType.NoopStorage.construct();
    public static PlatformDataType<ChunkAccess, ChunkData> CHUNK_DATA = PlatformDataType.NoopStorage.construct();

    public static <O, D> D ensureData(PlatformDataType<O, D> dataType, O object, Supplier<D> defaultData) {
        return dataType.ensureData(object, defaultData);
    }

    public static <O, D> D getData(PlatformDataType<O, D> dataType, O object) {
        return dataType.getData(object);
    }

    public static <O, D> void storeData(PlatformDataType<O, D> dataType, O object, D data) {
        dataType.storeData(object, data);
    }

    public static void reset() {
        PLAYER_DATA.reset();
    }
}
