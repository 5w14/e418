package ru.maxthetomas.e418.event.engine;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.event.cause.impl.GlobalRandomEventCause;
import ru.maxthetomas.e418.event.cause.impl.PlayerRandomEventCause;
import ru.maxthetomas.e418.event.registry.EventRegistries;

import java.util.*;

public class RandomEventManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Config CONFIG = E418.getConfig();
    private static final Random RANDOM = new Random();

    // TODO: Put this into config
    private static final int GROUP_DISTANCE = 50;
    private static final int TIME_OFFSET_PER_BLOCK = 900;

    private static int timeToEvent = 20 * 60 * 30;

    // Contains timestamps in ticks when event should be started
    // TODO: Better way to store it
    public static HashMap<UUID, Long> players = new HashMap<UUID, Long>();

    public RandomEventManager() {
        TickEvent.SERVER_POST.register(this::tick);
        refreshTimer();
    }

    private static void refreshTimer() {
        var config = EventRegistries.GLOBAL_RANDOM.getConfig();
        timeToEvent = config.getMinTime() + RANDOM.nextInt(config.getMaxTime() - config.getMinTime());
    }

    private long ensureTimestamp(UUID uuid, long currentTime) {
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else {
            // TODO: Load this from saves
            var time = currentTime + 1200L;
            players.put(uuid, time);
            return time;
        }
    }

    public void tick(MinecraftServer minecraftServer) {
        var currentTime = minecraftServer.overworld().getGameTime();

        if (minecraftServer.getPlayerCount() == 0)
            return;
        timeToEvent--;

        // Process player random events
        for (ServerPlayer player : minecraftServer.getPlayerList().getPlayers()) {
            var uuid = player.getUUID();
            var timestamp = ensureTimestamp(uuid, currentTime);

            if (timestamp < currentTime) {
                // Start event

                // TODO: Randomize delay
                var randomDelay = 1200L;
                players.put(uuid, currentTime + randomDelay);

                var cause = new PlayerRandomEventCause(uuid);
                EventRegistries.PLAYER_RANDOM.eventTick(cause);

                // Delay event time for players nearby
                if (!cause.isGroupEffectCancelled()) {
                    var playerPos = player.position();

                    for (ServerPlayer otherPlayer : minecraftServer.getPlayerList().getPlayers()) {
                        // No delay if you are in other dimension (and for yourself too)
                        if (otherPlayer == player || otherPlayer.level() != player.level()) {
                            continue;
                        }

                        var otherPlayerPos = otherPlayer.position();

                        // Players in range will have fixed offset to next event
                        if (playerPos.closerThan(otherPlayerPos, GROUP_DISTANCE)) {
                            var otherUuid = otherPlayer.getUUID();
                            var distance = playerPos.distanceTo(otherPlayerPos);
                            var timeOffset = (long) (randomDelay / distance);
                            //var timeOffset = (long) (TIME_OFFSET_PER_BLOCK * (GROUP_DISTANCE - distance));

                            players.put(otherUuid, ensureTimestamp(otherUuid, currentTime) + timeOffset);
                        }
                    }
                }

            }
        }

        /*
        // Process global random events
        if (timeToEvent <= 0) {
            var cause = new GlobalRandomEventCause();
            EventRegistries.GLOBAL_RANDOM.eventTick(cause);
            refreshTimer();
        }

         */
    }
}
