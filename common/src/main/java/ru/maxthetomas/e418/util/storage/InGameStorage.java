package ru.maxthetomas.e418.util.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import ru.maxthetomas.e418.E418;

public class InGameStorage extends SavedData {
    public static final MapCodec<InGameStorage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.PASSTHROUGH.fieldOf("kv_store").forGetter(a -> new Dynamic<Tag>(NbtOps.INSTANCE, a.keyValueStore))
    ).apply(instance, InGameStorage::constructByCodec));

    private static final Factory<InGameStorage> FACTORY = new Factory<>(InGameStorage::new,
            InGameStorage::constructFactory, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    public static InGameStorage INSTANCE = null;

    private CompoundTag keyValueStore = new CompoundTag();

    public void setValue(String key, Tag value) {
        keyValueStore.put(key, value);
    }

    public Dynamic<Tag> getValue(String key) {
        return new Dynamic<Tag>(NbtOps.INSTANCE, keyValueStore.get(key));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var result = (CompoundTag) CODEC.encode(this,
                NbtOps.INSTANCE, NbtOps.INSTANCE.mapBuilder()).build(compoundTag).getOrThrow();

        result.getAllKeys();
        return result;
    }

    public void save(MinecraftServer server) {
        save(server, this);
    }

    public static InGameStorage load(MinecraftServer server) {
        INSTANCE = server.overworld().getDataStorage().get(FACTORY, E418.MOD_ID);
        if (INSTANCE == null)
            INSTANCE = new InGameStorage();
        return INSTANCE;
    }

    public static void save(MinecraftServer server, InGameStorage inGameStorage) {
        server.overworld().getDataStorage().set(E418.MOD_ID, inGameStorage);
    }

    private static InGameStorage constructFactory(CompoundTag tag, HolderLookup.Provider provider) {
        return CODEC.decoder().decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst();
    }

    private static InGameStorage constructByCodec(Dynamic<?> dynamic) {
        var store = new InGameStorage();

        var tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        if (tag instanceof CompoundTag ct)
            store.keyValueStore = ct;

        return store;
    }
}
