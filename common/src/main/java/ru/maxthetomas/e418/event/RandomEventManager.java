package ru.maxthetomas.e418.event;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.event.registry.EventRegistries;

import java.util.Random;

public class RandomEventManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Config CONFIG = E418.getConfig().orElseThrow();
    private static final Random RANDOM = new Random();

    private static int currentTick = 0;
    private static int timeToEvent = RANDOM.nextInt(
            CONFIG.getMinTimeBetweenEvents(),
            CONFIG.getMaxTimeBetweenEvents());

    public static void init() {
        if (CONFIG.isRandomEventsEnabled()) {
            TickEvent.SERVER_POST.register(RandomEventManager::tick);
        }
    }

    public static void tick(MinecraftServer minecraftServer) {
        currentTick++;

        if (currentTick >= timeToEvent) {
            var eventResource = EventRegistries.RANDOM.getRandomEvent();

            var eventContext = new EventContext(minecraftServer);

            var activeEvent = E418.getEventManager().runEvent(eventResource, eventContext);

            if (activeEvent == null) {
                LOGGER.info("Failed to start random event: {}", eventResource.name());
            } else {
                LOGGER.info("Starting random event: {}", eventResource.name());
            }

            currentTick = 0;
            timeToEvent = RANDOM.nextInt(
                    CONFIG.getMinTimeBetweenEvents(),
                    CONFIG.getMaxTimeBetweenEvents());
        }
    }
}
