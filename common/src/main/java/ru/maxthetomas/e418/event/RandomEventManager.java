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
    private static final Config CONFIG = E418.getConfig();
    private static final Random RANDOM = new Random();

    private static int timeToEvent = 20 * 60 * 30;

    public static void init() {
        TickEvent.SERVER_POST.register(RandomEventManager::tick);
        refreshTimer();
    }

    private static void refreshTimer() {
        var config = EventRegistries.RANDOM.getConfig();
        timeToEvent = config.getMinTime() + RANDOM.nextInt(config.getMaxTime() - config.getMinTime());
    }

    public static void tick(MinecraftServer minecraftServer) {
        timeToEvent--;

        if (timeToEvent <= 0) {
            EventRegistries.RANDOM.eventTick(null);
            refreshTimer();
        }
    }
}
