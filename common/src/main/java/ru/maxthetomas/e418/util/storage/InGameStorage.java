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
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.ActiveEvent;
import ru.maxthetomas.e418.event.EventManager;
import ru.maxthetomas.e418.event.QueuedEvent;

import java.util.ArrayList;
import java.util.List;

public class InGameStorage extends SavedData {
    public static final MapCodec<InGameStorage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.PASSTHROUGH.fieldOf("kv_store")
                    .forGetter(a -> new Dynamic<Tag>(NbtOps.INSTANCE, a.keyValueStore)),
            ActiveEvent.CODEC.codec().listOf().fieldOf("active_events")
                    .forGetter(v -> v.activeEvents),
            QueuedEvent.CODEC.codec().listOf().fieldOf("queued_events")
                    .forGetter(v -> v.queuedEvents),
            Codec.LONG.fieldOf("global_event_tick")
                    .forGetter(v -> v.globalEventTick)
    ).apply(instance, InGameStorage::constructByCodec));

    private static final Factory<InGameStorage> FACTORY = new Factory<>(InGameStorage::new,
            InGameStorage::constructFactory, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    public static InGameStorage INSTANCE = null;

    private CompoundTag keyValueStore = new CompoundTag();
    private List<ActiveEvent> activeEvents = new ArrayList<>();
    private List<QueuedEvent> queuedEvents = new ArrayList<>();
    private Long globalEventTick;

    /**
     * Saves a value into a KV-store NBT store.
     * It's recommended to use {@link NbtOps} methods to construct values.
     * <p>
     * Example usage:
     * <blockquote><pre>
     * setValue("is_debug", NbtOps.INSTANCE.createBoolean(true))
     * </pre></blockquote>
     *
     * @param key   A string key to a key-value store.
     * @param value A Tag value for the key-value store.
     */
    public void setValue(String key, Tag value) {
        keyValueStore.put(key, value);
    }

    public Dynamic<Tag> getValue(String key) {
        return new Dynamic<Tag>(NbtOps.INSTANCE, keyValueStore.get(key));
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        if (EventManager.IsActive) {
            this.activeEvents = E418.getEventManager().getActiveEvents();
            this.queuedEvents = E418.getEventManager().getQueuedEvents();
        }

        return (CompoundTag) CODEC.encode(this,
                NbtOps.INSTANCE, NbtOps.INSTANCE.mapBuilder()).build(compoundTag).getOrThrow();
    }

    public static void load(MinecraftServer server) {
        INSTANCE = server.overworld().getDataStorage().get(FACTORY, E418.MOD_ID);
        if (INSTANCE == null)
            INSTANCE = new InGameStorage();

        server.overworld().getDataStorage().set(E418.MOD_ID, INSTANCE);
    }

    private static InGameStorage constructFactory(CompoundTag tag, HolderLookup.Provider provider) {
        return CODEC.decoder().decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst();
    }

    private static InGameStorage constructByCodec(Dynamic<?> dynamic, List<ActiveEvent> activeEvents, List<QueuedEvent> queuedEvents, Long globalEventTick) {
        var store = new InGameStorage();

        var tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        if (tag instanceof CompoundTag ct)
            store.keyValueStore = ct;

        E418.getEventManager()._restoreActiveEvents(activeEvents);
        E418.getEventManager()._restoreQueuedEvents(queuedEvents);
        E418.getEventEngine().RandomEventManager.GlobalEventTick = globalEventTick;

        return store;
    }
}
