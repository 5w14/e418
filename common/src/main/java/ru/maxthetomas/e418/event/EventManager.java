package ru.maxthetomas.e418.event;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.event.registry.EventRegistries;
import ru.maxthetomas.e418.event.registry.EventRegistry;
import ru.maxthetomas.e418.util.storage.InGameStorage;
import ru.maxthetomas.e418.util.storage.PlatformDataManager;

import java.util.*;

public class EventManager extends SimplePreparableReloadListener<EventManager.EventManagerData> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<ActiveEvent> activeEvents = new ArrayList<>();
    private final List<QueuedEvent> queuedEvents = new ArrayList<>();
    private Map<ResourceLocation, EventResource> registeredEvents;

    public static boolean IsActive = false;
    private static boolean hasErrored = false;

    public EventManager() {
        TickEvent.SERVER_POST.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        if (!IsActive) return;
        updateQueuedEvents(server);
        updateActiveEvents();
        activeEvents.forEach(ActiveEvent::tick);
        InGameStorage.INSTANCE.setDirty();
    }

    public void updateQueuedEvents(MinecraftServer server) {
        // Queued events
        queuedEvents.removeIf((QueuedEvent queuedEvent) -> {
            // Check timeout time
            if (queuedEvent.timeoutTick() != null && queuedEvent.timeoutTick() < server.overworld().getGameTime()) {
                LOGGER.info("Dequeued event {} (timed out).", queuedEvent.resource().name());
                return true;
            }

            // Run event if it can
            if (queuedEvent.resource().canRun(queuedEvent.context())) {
                LOGGER.info("Running event {} from queue.", queuedEvent.resource().name());
                E418.getEventManager().runEvent(queuedEvent.resource(), queuedEvent.context());
                return true;
            }
            return false;
        });

    }

    public void updateActiveEvents() {
        // Active events
        activeEvents.removeIf((ActiveEvent event) -> {
            // Check if dirty event is done
            if (event.isDirty() && event.isDone()) {
                event.disposeBehaviours();
                LOGGER.info("Disposed event {} (Event is done)", event.resource.name());
                return true;
            }
            event.undirty();
            return false;
        });
    }

    public void fullReset(@Nullable MinecraftServer server) {
        IsActive = false;

        for (int i = 0; i < activeEvents.size(); i++) {
            ActiveEvent activeEvent = activeEvents.get(i);
            disposeEvent(activeEvent);
            i--;
        }

        updateActiveEvents();
        if (server != null) {
            updateQueuedEvents(server);
            E418.getEventEngine().reset(server);
        }

        activeEvents.clear();
        queuedEvents.clear();
        PlatformDataManager.reset();
    }

    public void init() {
        IsActive = true;
    }

    public static boolean isErrored() {
        return hasErrored;
    }

    // Getters
    public List<ActiveEvent> getActiveEvents() {
        return List.copyOf(activeEvents);
    }

    public List<QueuedEvent> getQueuedEvents() {
        return List.copyOf(queuedEvents);
    }

    public @Nullable EventResource getEvent(ResourceLocation location) {
        return this.registeredEvents.getOrDefault(location, null);
    }

    public Set<ResourceLocation> getRegisteredEvents() {
        return registeredEvents.keySet();
    }

    /**
     * Gets active event's resource key
     */
    public ResourceLocation getResourceLocation(ActiveEvent activeEvent) {
        return getResourceLocation(activeEvent.resource);
    }

    public ResourceLocation getResourceLocation(EventResource resource) {
        for (Map.Entry<ResourceLocation, EventResource> entry : registeredEvents.entrySet()) {
            if (entry.getValue().equals(resource))
                return entry.getKey();
        }

        // Should never happen.
        return null;
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
            behaviour.tryExecute(context, activeEvent);
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

        event.stopBehaviours();
    }

    /**
     * Disposes the event.
     *
     * @param event Event to dispose.
     */
    public void disposeEvent(ActiveEvent event) {
        LOGGER.info("Disposed event {}", event.resource.name());

        event.disposeBehaviours();

        activeEvents.remove(event);
    }

    public boolean isDisposed(ActiveEvent event) {
        return !activeEvents.contains(event);
    }

    public void _restoreActiveEvents(List<ActiveEvent> restoredActiveEvents) {
        restoredActiveEvents.forEach(e -> {
            e.context.withSourceEvent(e);
            e.updateState();
            e._restoreState();
        });
        this.activeEvents.addAll(restoredActiveEvents);
    }

    public void _restoreQueuedEvents(List<QueuedEvent> restoredQueuedEvents) {
        this.queuedEvents.addAll(restoredQueuedEvents);
    }

    /*
     * Following methods are used to reload events from
     * resources, using Minecraft's reload system.
     */

    @Override
    protected @NotNull EventManagerData prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        try {
            profilerFiller.push("eventManagerPrepare");

            HashMap<ResourceLocation, EventResource> eventMap = new HashMap<>();
            SimpleJsonResourceReloadListener.scanDirectory(resourceManager, FileToIdConverter.json("events"),
                    JsonOps.INSTANCE, EventResource.CODEC.codec(), eventMap);

            HashMap<ResourceLocation, AddToRegistryData> addToRegistry = new HashMap<>();
            SimpleJsonResourceReloadListener.scanDirectory(resourceManager, FileToIdConverter.json("event_registries"),
                    JsonOps.INSTANCE, AddToRegistryData.CODEC.codec(), addToRegistry);

            profilerFiller.pop();

            return new EventManagerData(eventMap, addToRegistry, false);
        } catch (Exception e) {
            LOGGER.error("Could not prepare event manager data", e);
            return new EventManagerData(Map.of(), Map.of(), true);
        }
    }

    @Override
    protected void apply(EventManagerData object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        hasErrored = object.hasErrored;
        try {
            this.registeredEvents = object.events();

            EventRegistries.clearAll();

            for (Map.Entry<ResourceLocation, AddToRegistryData> entry : object.registryUpdate().entrySet()) {
                ResourceLocation key = entry.getKey();
                AddToRegistryData value = entry.getValue();
                var registry = EventRegistries.addRegistry(key);

                value.storedWeightedEvents().forEach(stored ->
                        registry.addEvent(E418.getEventManager().getEvent(stored.id)));

                value.storedTags().forEach(registry::addTag);
            }

            LOGGER.info("Successfully reloaded events");
        } catch (Exception e) {
            LOGGER.error("Could not reload events", e);
        }

        if (hasErrored && Config.isDebug()) {
            E418.getCurrentServer().ifPresent(server -> {
                server.getPlayerList().broadcastSystemMessage(Component.translatable("e418.notice.error.loading_error")
                        .withStyle(ChatFormatting.RED), false);
            });
        }
    }


    public record EventManagerData(Map<ResourceLocation, EventResource> events,
                                   Map<ResourceLocation, AddToRegistryData> registryUpdate, boolean hasErrored) {
    }

    public record AddToRegistryData(List<StoredWeightedEvent> storedWeightedEvents, List<String> storedTags) {
        public static final MapCodec<AddToRegistryData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StoredWeightedEvent.CODEC.listOf().fieldOf("events").forGetter(AddToRegistryData::storedWeightedEvents),
                Codec.STRING.listOf().fieldOf("tags").forGetter(AddToRegistryData::storedTags)
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
            var event = E418.getEventManager().getEvent(id);
            if (event == null) {
                LOGGER.warn("Cannot find event {}!", id);
                return null;
            }
            return new EventRegistry.WeightedEvent(event, weight);
        }
    }
}
