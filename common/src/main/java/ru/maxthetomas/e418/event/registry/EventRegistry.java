package ru.maxthetomas.e418.event.registry;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import ru.maxthetomas.e418.event.EventResource;
import ru.maxthetomas.e418.util.WeightedList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Registry that contains events.
 * <br>
 * See {@linkplain EventRegistries} for the list of registries.
 */
public class EventRegistry {
    protected static final Logger LOGGER = LogUtils.getLogger();
    protected final WeightedList<EventResource> events = new WeightedList<>();
    protected final Set<String> tags = new HashSet<>();
    protected final ResourceLocation id;

    public EventRegistry(ResourceLocation id) {
        this.id = id;
    }

    public void clear() {
        events.clear();
        tags.clear();
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
    public EventResource getRandomEvent(RandomSource random) {
        return events.getRandomElement(random);
    }

    public List<WeightedList.Entry<EventResource>> getEvents() {
        return events.values;
    }

    public Set<String> getTags() {
        return tags;
    }

    public ResourceLocation getId() {
        return id;
    }

    public record WeightedEvent(EventResource resource, int weight) {
    }
}
