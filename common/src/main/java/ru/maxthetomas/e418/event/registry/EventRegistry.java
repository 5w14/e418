package ru.maxthetomas.e418.event.registry;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import ru.maxthetomas.e418.config.SourceConfig;
import ru.maxthetomas.e418.event.EventResource;
import ru.maxthetomas.e418.util.WeightedList;

import java.util.List;
import java.util.Random;

/**
 * Registry that contains events. Can be configured through from config through {@linkplain SourceConfig} and it's variations.
 * <br>
 * See {@linkplain EventRegistries} for the list of registries.
 */
public class EventRegistry {
    protected static final Logger LOGGER = LogUtils.getLogger();
    protected WeightedList<EventResource> events = new WeightedList<>();
    protected List<String> tags = List.of();
    protected ResourceLocation id;

    public EventRegistry(ResourceLocation id) {
        this.id = id;
    }

    public void clear() {
        events.clear();
    }

    public void addEvent(EventResource resource) {
        events.add(1, resource);
    }

    public void addEvent(int weight, EventResource resource) {
        events.add(weight, resource);
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Gets a random event from the registry, calculated using weights.
     *
     * @param random Random number supplier to use for selecting an event.
     * @return Randomly picked event.
     */
    public EventResource getRandomEvent(Random random) {
        return events.getRandomElement(random);
    }

    /**
     * Executes {@code getRandomEvent} with a new {@linkplain Random}
     */
    public EventResource getRandomEvent() {
        // Todo: avoid using new Random()'s
//        LOGGER.warn("New random created when getting random event in registry {}", id);
        return getRandomEvent(new Random());
    }

    public List<WeightedList.Entry<EventResource>> getEvents() {
        return events.values;
    }

    public List<String> getTags() {
        return tags;
    }

    public ResourceLocation getId() {
        return id;
    }

    public record WeightedEvent(EventResource resource, int weight) { }
}
