package ru.maxthetomas.e418.event.registry;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import ru.maxthetomas.e418.config.SourceConfig;
import ru.maxthetomas.e418.event.EventResource;
import ru.maxthetomas.e418.event.cause.IEventCause;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Registry that contains events. Can be configured through from config through {@linkplain SourceConfig} and it's variations.
 * <br>
 * See {@linkplain EventRegistries} for the list of registries.
 */
public abstract class EventRegistry<Cfg extends SourceConfig> {
    protected static final Logger LOGGER = LogUtils.getLogger();
    protected final List<WeightedEvent> events = new ArrayList<>();

    protected Cfg config;

    public EventRegistry() {
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


    /**
     * Should be triggered any time the event could be triggered.
     * For random, this would be when there are no ticks until next event.
     */
    public boolean eventTick(IEventCause cause) {
        if (!this.config.isEnabled()) return false;
        if (RandomSource.create().nextFloat() > this.config.getChance()) return false;
        startEvent(cause);
        return true;
    }

    /**
     * Actually starts the event.
     */
    protected abstract void startEvent(IEventCause cause);

    public List<WeightedEvent> getEvents() {
        return events;
    }

    public Cfg getConfig() {
        return config;
    }

    public abstract ResourceLocation getId();

    public record WeightedEvent(EventResource resource, int weight) {
    }
}
