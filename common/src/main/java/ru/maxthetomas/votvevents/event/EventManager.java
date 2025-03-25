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
import ru.maxthetomas.votvevents.util.ResourceUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EventManager extends SimplePreparableReloadListener<HashMap<ResourceLocation, EventResource>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private List<ActiveEvent> activeEvents = new ArrayList<>();
    private HashMap<ResourceLocation, EventResource> registeredEvents;

    @Override
    protected @NotNull HashMap<ResourceLocation, EventResource> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        var events = new HashMap<ResourceLocation, EventResource>();

        resourceManager.listResources("events", (path) -> path.getPath().endsWith(".json")).forEach((loc, resource) -> {
            var evt = ResourceUtil.getJsonResource(resourceManager, loc);

            if (evt == null || !evt.isJsonObject()) {
                LOGGER.warn("Failed to parse event resource at location {}", loc);
                return;
            }

            var res = EventResource.buildEventResourceFromJson(evt.getAsJsonObject());

            if (res == null) {
                LOGGER.warn("Failed to parse event resource at location {}", loc);
                return;
            }

            var ns = loc.getNamespace();
            var p = loc.getPath().replace(".json", "").replace("events/", "");

            events.put(ResourceLocation.fromNamespaceAndPath(ns, p), res);
        });

        return events;
    }

    @Override
    protected void apply(HashMap<ResourceLocation, EventResource> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.registeredEvents = object;
        LOGGER.info("Successfully reloaded events!");
    }

    public List<ActiveEvent> getActiveEvents() {
        return activeEvents;
    }

    /**
     * Runs an event.
     *
     * @param resource Resource of event that should be run.
     * @param context  Context of run. This also gets source event if event launches.
     * @param forced   If true, event will run even if run conditions are not met. This doesn't bypass run conditions for behaviours.
     * @return New active event or null, if event wasn't launched.
     */
    public ActiveEvent runEvent(EventResource resource, EventContext context, boolean forced) {
        if (!resource.canRun(context, forced))
            return null;

        var activeEvent = new ActiveEvent(resource, context, context.getServer().overworld().getGameTime());
        context.withSourceEvent(activeEvent);

        activeEvents.add(activeEvent);

        for (IBehaviour behaviour : activeEvent.resource.behaviourList) {
            behaviour.execute(context);
        }

        return activeEvent;
    }

    /**
     * Stops the event.
     *
     * @param event Event to stop.
     */
    public void endEvent(ActiveEvent event) {
        for (IBehaviour behaviour : event.resource.behaviourList) {
            behaviour.dispose(event.context);
        }

        activeEvents.remove(event);
    }

    public boolean isNotDisposed(ActiveEvent event) {
        return activeEvents.contains(event);
    }

    public @Nullable EventResource getEvent(ResourceLocation location) {
        return this.registeredEvents.getOrDefault(location, null);
    }

    public Set<ResourceLocation> getRegisteredEvents() {
        return registeredEvents.keySet();
    }
}
