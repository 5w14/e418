package ru.maxthetomas.votvevents.event;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class EventManager extends SimplePreparableReloadListener<Map<ResourceLocation, EventResource>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<ActiveEvent> activeEvents = new ArrayList<>();
    private final List<QueuedEvent> queuedEvents = new ArrayList<>();
    private Map<ResourceLocation, EventResource> registeredEvents;

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

    public EventManager() {
        TickEvent.SERVER_POST.register(EventManager::tick);
    }

    private static void tick(MinecraftServer server) {
        VotvEvents.getEventManager().queuedEvents.removeIf((QueuedEvent queuedEvent) -> {
            // Check timeout time
            if (queuedEvent.timeoutTick != null && queuedEvent.timeoutTick < server.overworld().getGameTime()) {
                LOGGER.info("Dequeued event {} (timed out).", queuedEvent.resource.name());
                return true;
            }

            // Run event if it can
            if (queuedEvent.resource.canRun(queuedEvent.context)) {
                LOGGER.info("Running event {} from queue.", queuedEvent.resource.name());
                VotvEvents.getEventManager().runEvent(queuedEvent.resource, queuedEvent.context);
                return true;
            }
            return false;
        });
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
        LOGGER.info("Dequeued event {}", queuedEvent.resource.name());

        queuedEvents.remove(queuedEvent);
    }

    /**
     * Stops the event.
     *
     * @param event Event to stop.
     */
    public void endEvent(ActiveEvent event) {
        LOGGER.info("Ended event {}", event.resource.name());

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
    protected @NotNull Map<ResourceLocation, EventResource> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        HashMap<ResourceLocation, EventResource> eventMap = new HashMap<>();

        var fileToId = FileToIdConverter.json("events");
        for (Map.Entry<ResourceLocation, Resource> entry : fileToId.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            ResourceLocation resourceLocation2 = fileToId.fileToId(resourceLocation);
            try {
                BufferedReader reader = entry.getValue().openAsReader();
                try {
                    EventResource.CODEC.decoder().decode(JsonOps.INSTANCE,
                            JsonParser.parseReader(reader)).ifSuccess(event -> {
                        if (eventMap.putIfAbsent(resourceLocation2, event.getFirst()) != null) {
                            throw new IllegalStateException("Duplicate data file ignored with ID " + String.valueOf(resourceLocation2));
                        }
                    }).ifError(error -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", resourceLocation2, resourceLocation, error));
                } finally {
                    ((Reader) reader).close();
                }
            } catch (JsonParseException | IOException | IllegalArgumentException exception) {
                LOGGER.error("Couldn't parse data file '{}' from '{}'", resourceLocation2, resourceLocation, exception);
            }
        }

        return eventMap;
    }

    @Override
    protected void apply(Map<ResourceLocation, EventResource> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.registeredEvents = object;
        LOGGER.info("Successfully reloaded events!");
    }
}
