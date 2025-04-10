package ru.maxthetomas.e418.event.registry;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.registry.impl.RandomEventRegistry;
import ru.maxthetomas.e418.event.registry.impl.SimpleEventRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EventRegistries {
    private static final Map<ResourceLocation, EventRegistry<?>> REGISTRY = new HashMap<>();

    // Register
    public static final RandomEventRegistry RANDOM = create(new RandomEventRegistry());
    public static final SimpleEventRegistry WAKE_UP = create("wake_up", 0.03F);

    public static Optional<? extends EventRegistry<?>> get(ResourceLocation id) {
        return Optional.ofNullable(REGISTRY.getOrDefault(id, null));
    }

    public static Set<ResourceLocation> getRegistries() {
        return REGISTRY.keySet();
    }

    public static void clearAll() {
        REGISTRY.values().forEach(EventRegistry::clear);
    }

    public static <T extends EventRegistry<?>> T create(T instance) {
        REGISTRY.put(instance.getId(), instance);
        return instance;
    }

    private static SimpleEventRegistry create(String id, float defaultChance) {
        return create(new SimpleEventRegistry(ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, id), defaultChance));
    }
}
