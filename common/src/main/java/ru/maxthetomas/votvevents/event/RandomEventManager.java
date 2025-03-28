package ru.maxthetomas.votvevents.event;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.config.Config;

import java.util.Random;

public class RandomEventManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Config CONFIG = VotvEvents.getConfig().orElseThrow();
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
            var eventResource = VotvEvents.getEventManager().getRandomEvent();

            var eventContext = new EventContext(minecraftServer)
                    .withPlayer(minecraftServer.overworld().getRandomPlayer());

            LOGGER.info("Starting random event: {}", eventResource.name());
            VotvEvents.getEventManager().runEvent(eventResource, eventContext);

            currentTick = 0;
            timeToEvent = RANDOM.nextInt(
                    CONFIG.getMinTimeBetweenEvents(),
                    CONFIG.getMaxTimeBetweenEvents());
        }
    }
}
