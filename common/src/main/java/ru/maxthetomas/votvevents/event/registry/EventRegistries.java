package ru.maxthetomas.votvevents.event.registry;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventRegistries {
    public static final EventRegistry RANDOM = create("random");

    private static final Map<ResourceLocation, EventRegistry> REGISTRY = new HashMap<>();

    public static Optional<EventRegistry> get(ResourceLocation id) {
        return Optional.ofNullable(REGISTRY.getOrDefault(id, null));
    }

    public static void clearAll() {
        REGISTRY.values().forEach(EventRegistry::clear);
    }

    public static EventRegistry create(ResourceLocation id) {
        return new EventRegistry(id);
    }

    private static EventRegistry create(String id) {
        return create(ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, id));
    }
}
