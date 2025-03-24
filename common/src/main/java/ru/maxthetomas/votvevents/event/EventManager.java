package ru.maxthetomas.votvevents.event;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.util.ResourceUtil;

import java.util.List;

public class EventManager extends SimplePreparableReloadListener<List<JsonElement>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    List<JsonElement> registeredEvents;

    @Override
    protected @NotNull List<JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        var event = ResourceUtil.getJsonResource(resourceManager, "events/test_event.json");

        if (event == null) {
            LOGGER.warn("Could not load event file!");
            return List.of();
        }

        return List.of(event);
    }

    @Override
    protected void apply(List<JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.registeredEvents = object;
        LOGGER.info("Successfully reloaded events!");
    }
}
