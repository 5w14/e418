package ru.maxthetomas.votvevents.event;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.util.ResourceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager extends SimplePreparableReloadListener<HashMap<ResourceLocation, EventResource>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    public List<ActiveEvent> activeEvents = new ArrayList<>();
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

            events.put(loc, res);
        });

        return events;
    }

    @Override
    protected void apply(HashMap<ResourceLocation, EventResource> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.registeredEvents = object;
        LOGGER.info("Successfully reloaded events!");
    }

    public ActiveEvent runEvent(EventResource resource, EventContext context, boolean forced) {
        if (resource.canRun(context, forced))
            return null;


        return null;
    }
}
