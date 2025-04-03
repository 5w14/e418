package ru.maxthetomas.votvevents.event;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.registry.EventRegistries;
import ru.maxthetomas.votvevents.event.registry.EventRegistry;

import java.util.*;

public class EventManager extends SimplePreparableReloadListener<EventManager.EventManagerData> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<ActiveEvent> activeEvents = new ArrayList<>();
    private final List<QueuedEvent> queuedEvents = new ArrayList<>();
    private Map<ResourceLocation, EventResource> registeredEvents;

    public EventManager() {
        TickEvent.SERVER_POST.register(EventManager::tick);
    }

    private static void tick(MinecraftServer server) {
        VotvEvents.getEventManager().queuedEvents.removeIf((QueuedEvent queuedEvent) -> {
            // Check timeout time
            if (queuedEvent.timeoutTick() != null && queuedEvent.timeoutTick() < server.overworld().getGameTime()) {
                LOGGER.info("Dequeued event {} (timed out).", queuedEvent.resource().name());
                return true;
            }

            // Run event if it can
            if (queuedEvent.resource().canRun(queuedEvent.context())) {
                LOGGER.info("Running event {} from queue.", queuedEvent.resource().name());
                VotvEvents.getEventManager().runEvent(queuedEvent.resource(), queuedEvent.context());
                return true;
            }
            return false;
        });
    }

    // Getters
    public List<ActiveEvent> getActiveEvents() {
        return activeEvents;
    }

    public List<QueuedEvent> getQueuedEvents() {
        return queuedEvents;
    }

    public @Nullable EventResource getEvent(ResourceLocation location) {
        return this.registeredEvents.getOrDefault(location, null);
    }

    /**
     * TODO: Implement event list with weights.
     * Returns random registered event.
     *
     * @return random registered event
     */
    public EventResource getRandomEvent() {
        Random random = new Random();
        var resourceArray = this.registeredEvents.keySet().toArray();

        return this.registeredEvents.get((ResourceLocation) resourceArray[random.nextInt(resourceArray.length)]);
    }

    public Set<ResourceLocation> getRegisteredEvents() {
        return registeredEvents.keySet();
    }

    /**
     * Runs an event.
     *
     * @param resource Resource of event that should be run.
     * @param context  Context of run. This also gets source event if event launches.
     * @return New active event or null, if event wasn't launched.
     */
    public ActiveEvent runEvent(EventResource resource, EventContext context) {
        if (!resource.canRun(context))
            return null;

        var activeEvent = new ActiveEvent(resource, context, context.getServer().overworld().getGameTime());
        context.withSourceEvent(activeEvent);

        activeEvents.add(activeEvent);

        LOGGER.info("Started event {}", resource.name());

        for (var preActiveBehaviour : activeEvent.resource.behaviourList()) {
            var behaviour = preActiveBehaviour.create();
            behaviour.execute(context);
            activeEvent.activeBehaviours.add(behaviour);
        }

        activeEvent.updateState();

        return activeEvent;
    }

    /**
     * Adds event to queue. Queued events will run the moment their run conditions are met.
     *
     * @param resource Event to queue.
     * @param context  Context of event.
     * @param timeout  Time for event to be dequeued.
     * @return Is event was queued or was run instantly.
     */
    public boolean queueEvent(EventResource resource, EventContext context, int timeout) {
        var activeEvent = runEvent(resource, context);
        if (activeEvent != null)
            return true;

        if (!resource.canQueue(context))
            return false;

        // TODO: something bad could happen with context if we would use it later. Should be fixed.
        long timeoutTick;

        timeoutTick = context.getServer().overworld().getGameTime() + timeout;

        queuedEvents.add(new QueuedEvent(resource, context, timeoutTick));
        LOGGER.info("Queued event {}.", resource.name());
        return true;
    }

    /**
     * Adds event to queue. Queued events will run the moment their run conditions are met.
     *
     * @param resource Event to queue.
     * @param context  Context of event.
     * @return Is event was queued or was run instantly.
     */
    public boolean queueEvent(EventResource resource, EventContext context) {
        var activeEvent = runEvent(resource, context);
        if (activeEvent != null)
            return true;

        if (!resource.canQueue(context))
            return false;

        // TODO: something bad could happen with context if we would use it later. Should be fixed.
        queuedEvents.add(new QueuedEvent(resource, context, null));
        LOGGER.info("Queued event {}.", resource.name());
        return true;
    }

    public void dequeueEvent(QueuedEvent queuedEvent) {
        LOGGER.info("Dequeued event {}", queuedEvent.resource().name());

        queuedEvents.remove(queuedEvent);
    }

    /**
     * Stops the event.
     *
     * @param event Event to stop.
     */
    public void stopEvent(ActiveEvent event) {
        LOGGER.info("Stopping event {}", event.resource.name());

        for (IBehaviour behaviour : event.activeBehaviours) {
            behaviour.stop();
        }
    }

    /**
     * Disposes the event.
     *
     * @param event Event to dispose.
     */
    public void disposeEvent(ActiveEvent event) {
        LOGGER.info("Disposed event {}", event.resource.name());

        for (IBehaviour behaviour : event.activeBehaviours) {
            behaviour.dispose();
        }

        activeEvents.remove(event);
    }

    public boolean isNotDisposed(ActiveEvent event) {
        return activeEvents.contains(event);
    }
    /*
     * Following methods are used to reload events from
     * resources, using Minecraft's reload system.
     */

    @Override
    protected @NotNull EventManagerData prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        HashMap<ResourceLocation, EventResource> eventMap = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(resourceManager, FileToIdConverter.json("events"),
                JsonOps.INSTANCE, EventResource.CODEC.codec(), eventMap);

        HashMap<ResourceLocation, AddToRegistryData> addToRegistry = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(resourceManager, FileToIdConverter.json("event_registries"),
                JsonOps.INSTANCE, AddToRegistryData.CODEC.codec(), addToRegistry);

        return new EventManagerData(eventMap, addToRegistry);
    }

    @Override
    protected void apply(EventManagerData object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.registeredEvents = object.events();

        // Following code adds events to event registry by the resource path.

        EventRegistries.clearAll();

        // Due to limitations of file names on Windows, a file
        // cannot be named votvevents:random.json, so to avoid that we use folder structure:
        // event_registries/votvevents/random.json links to votvevents:random registry.
        object.registryUpdate().forEach((key, value) -> {
            var path = key.getPath().split("/");

            if (path.length <= 1) {
                return;
            }

            var location = ResourceLocation.fromNamespaceAndPath(path[0], path[1]);
            var registry = EventRegistries.get(location);

            if (registry.isEmpty()) {
                LOGGER.warn("Could not add events from {}, no such registry exists: {}", key, location);
                return;
            }

            var reg = registry.get();
            value.storedWeightedEvents().forEach(stored -> {
                reg.addEvent(stored.toWeightedEvent());
            });
        });

        LOGGER.info("Successfully reloaded events!");
    }

    public record EventManagerData(Map<ResourceLocation, EventResource> events,
                                   Map<ResourceLocation, AddToRegistryData> registryUpdate) {
    }

    public record AddToRegistryData(List<StoredWeightedEvent> storedWeightedEvents) {
        public static final MapCodec<AddToRegistryData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StoredWeightedEvent.CODEC.listOf().fieldOf("events").forGetter(AddToRegistryData::storedWeightedEvents)
        ).apply(instance, AddToRegistryData::new));
    }

    public record StoredWeightedEvent(ResourceLocation id, int weight) {
        public static final MapCodec<StoredWeightedEvent> FULL_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(StoredWeightedEvent::id),
                Codec.INT.optionalFieldOf("weight", 1).forGetter(StoredWeightedEvent::weight)
        ).apply(instance, StoredWeightedEvent::new));

        public static final Codec<StoredWeightedEvent> CODEC = Codec.withAlternative(
                FULL_CODEC.codec(),
                ResourceLocation.CODEC.xmap(id -> new StoredWeightedEvent(id, 1), StoredWeightedEvent::id)
        );

        public EventRegistry.WeightedEvent toWeightedEvent() {
            var event = VotvEvents.getEventManager().getEvent(id);
            if (event == null) {
                LOGGER.warn("Cannot find event {}!", id);
                return null;
            }
            return new EventRegistry.WeightedEvent(event, weight);
        }
    }
}
