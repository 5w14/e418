package ru.maxthetomas.e418.event.registry;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EventRegistries {
    private static final Map<ResourceLocation, EventRegistry> REGISTRY = new HashMap<>();

    // Register registries
    public static final EventRegistry RANDOM = create("random");
    public static final EventRegistry WAKE_UP = create("wake_up");

    public static Optional<EventRegistry> get(ResourceLocation id) {
        return Optional.ofNullable(REGISTRY.getOrDefault(id, null));
    }

    public static Set<ResourceLocation> getRegistries() {
        return REGISTRY.keySet();
    }

    public static void clearAll() {
        REGISTRY.values().forEach(EventRegistry::clear);
    }

    public static EventRegistry create(ResourceLocation id) {
        var registry = new EventRegistry(id);
        REGISTRY.put(id, registry);
        return registry;
    }

    private static EventRegistry create(String id) {
        return create(ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, id));
    }
}
