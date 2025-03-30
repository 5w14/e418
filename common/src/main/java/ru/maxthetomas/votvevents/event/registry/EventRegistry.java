package ru.maxthetomas.votvevents.event.registry;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.event.EventResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Registry that contains events. See {@linkplain EventRegistries} for the list of registries.
 */
public class EventRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation id;
    private final List<WeightedEvent> events = new ArrayList<>();

    public EventRegistry(ResourceLocation id) {
        this.id = id;
    }

    public void clear() {
        events.clear();
    }

    public void addEvent(WeightedEvent weightedEvent) {
        if (weightedEvent == null) return;
        events.add(weightedEvent);
    }

    public void addEvent(EventResource resource, int weight) {
        addEvent(new WeightedEvent(resource, weight));
    }

    public void addEvent(EventResource resource) {
        addEvent(resource, 1);
    }

    /**
     * Gets a random event from the registry, calculated using weights.
     *
     * @param random Random number supplier to use for selecting an event.
     * @return Randomly picked event.
     */
    public EventResource getRandomEvent(Random random) {
        if (events.isEmpty()) {
            return null;
        }
        
        int totalWeight = 0;
        for (WeightedEvent event : events) {
            totalWeight += event.weight();
        }

        int randomWeight = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (WeightedEvent event : events) {
            cumulativeWeight += event.weight;
            if (randomWeight < cumulativeWeight) {
                return event.resource();
            }
        }

        return events.getLast().resource();
    }

    /**
     * Executes {@code getRandomEvent} with a new {@linkplain Random}
     */
    public EventResource getRandomEvent() {
        // Todo: avoid using new Random()'s
//        LOGGER.warn("New random created when getting random event in registry {}", id);
        return getRandomEvent(new Random());
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<WeightedEvent> getEvents() {
        return events;
    }

    public record WeightedEvent(EventResource resource, int weight) {
    }
}
