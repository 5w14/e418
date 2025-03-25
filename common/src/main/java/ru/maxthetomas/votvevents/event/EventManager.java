package ru.maxthetomas.votvevents.event;

import com.mojang.logging.LogUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class EventManager extends SimplePreparableReloadListener<List<EventResource>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private List<EventResource> registeredEvents;

    @Override
    protected @NotNull List<EventResource> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        var events = new ArrayList<EventResource>();

        resourceManager.listResources("events", (path) -> path.getPath().endsWith(".json")).forEach((a, b) -> {
            var evt = ResourceUtil.getJsonResource(resourceManager, a);

            if (evt == null || !evt.isJsonObject())
                return;

            // TODO: change constructor to a static generator method
            var res = new EventResource(evt.getAsJsonObject());
            events.add(res);
        });

        return events;
    }

    @Override
    protected void apply(List<EventResource> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.registeredEvents = object;
        LOGGER.info("Successfully reloaded events!");
    }
}
