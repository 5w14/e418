package ru.maxthetomas.votvevents.event;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class EventManager extends SimplePreparableReloadListener<Map<ResourceLocation, EventResource>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<ActiveEvent> activeEvents = new ArrayList<>();
    private Map<ResourceLocation, EventResource> registeredEvents;

    // Getters
    public List<ActiveEvent> getActiveEvents() {
        return activeEvents;
    }

    public @Nullable EventResource getEvent(ResourceLocation location) {
        return this.registeredEvents.getOrDefault(location, null);
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
