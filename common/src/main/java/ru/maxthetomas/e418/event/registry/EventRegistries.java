package ru.maxthetomas.e418.event.registry;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.EventResource;
import ru.maxthetomas.e418.util.WeightedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/// This class registers all {@linkplain EventRegistry} instances.
public class EventRegistries {
    private static final Map<ResourceLocation, EventRegistry> REGISTRY = new HashMap<>();

    // Register
    public static final EventRegistry GLOBAL_RANDOM = create("global_random");
    public static final EventRegistry PLAYER_RANDOM = create("player_random");
    public static final EventRegistry WAKE_UP = create("wake_up");
    public static final EventRegistry CHAT_MESSAGE = create("chat_message");

    public static EventRegistry get(ResourceLocation id) {
        return REGISTRY.getOrDefault(id, null);
    }

    public static Set<ResourceLocation> getRegistries() {
        return REGISTRY.keySet();
    }

    public static List<EventRegistry> getRegistriesWithTag(String tag) {
        return REGISTRY.values().stream().filter((r) -> r.getTags().contains(tag)).toList();
    }

    /**
     * @param tag Event cause
     * @return A weighted list of events from registries with specified tag
     */
    public static WeightedList<EventResource> getEventsWithTag(String tag) {
        var weightedList = new WeightedList<EventResource>();

        var regs = getRegistriesWithTag(tag);

        for (EventRegistry reg : regs) {
            for (WeightedList.Entry<EventResource> e : reg.events.values) {
                weightedList.add(e.weight(), e.element());
            }
        }

        return weightedList;
    }

    /**
     * @param tag Event cause
     * @param ctx Context of event
     * @return A weighted list of events from registries with specified tag and that can be queued.
     */
    public static WeightedList<EventResource> getQueueableEventsWithTag(String tag, EventContext ctx) {
        var weightedList = new WeightedList<EventResource>();

        var regs = getRegistriesWithTag(tag);

        for (EventRegistry reg : regs) {
            for (WeightedList.Entry<EventResource> e : reg.events.values) {
                if (e.element().canQueue(ctx)) {
                    weightedList.add(e.weight(), e.element());
                }
            }
        }

        return weightedList;
    }

    public static void clearAll() {
        REGISTRY.values().forEach(EventRegistry::clear);
    }

    private static EventRegistry create(String id) {
        var instance = new EventRegistry(ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, id));
        REGISTRY.put(instance.getId(), instance);
        return instance;
    }
}
