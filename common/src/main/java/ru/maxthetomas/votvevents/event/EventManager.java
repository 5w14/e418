package ru.maxthetomas.votvevents.event;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventManager extends SimplePreparableReloadListener<Map<ResourceLocation, EventResource>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private Map<ResourceLocation, EventResource> registeredEvents;
    private List<ActiveEvent> activeEvents = new ArrayList<>();

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
     * @param forced   If true, event will run even if run conditions are not met. This doesn't bypass run conditions for behaviours.
     * @return New active event or null, if event wasn't launched.
     */
    public ActiveEvent runEvent(EventResource resource, EventContext context) {
        if (!resource.canRun(context))
            return null;

        var activeEvent = new ActiveEvent(resource, context, context.getServer().overworld().getGameTime());
        context.withSourceEvent(activeEvent);

        activeEvents.add(activeEvent);

        LOGGER.info("Started event {}", resource.name);

        for (var preActiveBehaviour : activeEvent.resource.behaviourList) {
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
        LOGGER.info("Ended event {}", event.resource.name);

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
//        SimpleJsonResourceReloadListener.scanDirectory(resourceManager, "events", (path) -> path.endsWith(".json"));
//        var events = new HashMap<ResourceLocation, EventResource>();
//
//        resourceManager.listResources("events", (path) -> path.getPath().endsWith(".json")).forEach((loc, resource) -> {
//            var evt = ResourceUtil.getJsonResource(resourceManager, loc);
//
//            if (evt == null || !evt.isJsonObject()) {
//                LOGGER.warn("Failed to parse event resource at location {}", loc);
//                return;
//            }
//
//            var res = EventResource.buildEventResourceFromJson(evt.getAsJsonObject());
//
//            if (res == null) {
//                LOGGER.warn("Failed to parse event resource at location {}", loc);
//                return;
//            }
//
//            var ns = loc.getNamespace();
//            var p = loc.getPath()
//                    .replaceFirst(".json", "")
//                    .replaceFirst("events/", "");
//
//            events.put(ResourceLocation.fromNamespaceAndPath(ns, p), res);
//        });
//
//        return events;

        return Map.of();
    }

    @Override
    protected void apply(Map<ResourceLocation, EventResource> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.registeredEvents = object;
//        LOGGER.info("Successfully reloaded events!");
    }
}
