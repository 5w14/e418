package ru.maxthetomas.e418.event.engine;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.GlobalRandomEventCause;
import ru.maxthetomas.e418.event.cause.impl.PlayerRandomEventCause;
import ru.maxthetomas.e418.event.registry.EventRegistries;
import ru.maxthetomas.e418.player.PlayerDataManager;
import ru.maxthetomas.e418.util.E418Random;
import ru.maxthetomas.e418.util.Location;

public class RandomEventManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    // TODO: Put this into config
    private static final int GROUP_DISTANCE = 50;

    private static int timeToGlobalEvent = 20 * 60 * 30;

    public RandomEventManager() {
        TickEvent.SERVER_POST.register(this::tick);
        // TODO: Load time from save data
    }

    public static int getTimeToGlobalEvent() {
        return timeToGlobalEvent;
    }

    public void tick(MinecraftServer minecraftServer) {
        var currentTime = minecraftServer.overworld().getGameTime();

        if (minecraftServer.getPlayerCount() == 0)
            return;
        timeToGlobalEvent--;

        // Process player random events
        for (ServerPlayer player : minecraftServer.getPlayerList().getPlayers()) {
            var uuid = player.getUUID();
            var data = PlayerDataManager.ensureData(uuid, minecraftServer);

            if (data.eventTimestamp < currentTime) {
                var random = E418Random.EVENT_ENGINE_GLOBAL;
                // Start event

                // Get nearby players
                var playersInRange = minecraftServer.getPlayerList().getPlayers().stream().filter((p) -> {
                    return (p != player &&
                            p.level() == player.level() &&
                            player.position().closerThan(p.position(), GROUP_DISTANCE));
                }).toList();

                var hasLocks = playersInRange.stream().anyMatch((p) -> {
                    var pdata = PlayerDataManager.ensureData(p.getUUID(), minecraftServer);

                    return pdata.eventUnlockTimestamp > currentTime;
                });

                // We prevent event start if there's a player with lock nearby.
                if (hasLocks) {
                    // TODO: Randomize delay with config
                    var randomDelay = random.nextInt(600, 1200);
                    data.eventTimestamp = currentTime + randomDelay;
                    continue;
                }


                var cause = new PlayerRandomEventCause(uuid);

                var success = false;
                var ctx = new EventContext(minecraftServer)
                        .withPlayer(player)
                        .withLocation(Location.fromPlayer(player))
                        .withCause(cause);
                var e = EventRegistries.getQueueableEventsWithTag("random.player", ctx).getRandomElement(random);

                if (e != null) {
                    success = E418.getEventManager().queueEvent(e, ctx);
                }

                if (success) {
                    // TODO: Randomize delay with config

                    var randomDelay = random.nextInt(600, 1200);
                    var randomOffset = random.nextInt(600, 1200);
                    var randomLock = random.nextInt(2000, 2400);

                    data.eventTimestamp = currentTime + randomDelay + randomOffset;
                    data.eventUnlockTimestamp = currentTime + randomLock;

                    // Delay event time for players nearby
                    if (success && !cause.isGroupEffectCancelled() && GROUP_DISTANCE > 0) {
                        var playerPos = player.position();

                        for (ServerPlayer otherPlayer : playersInRange) {
                            var otherUuid = otherPlayer.getUUID();
                            var otherData = PlayerDataManager.ensureData(otherUuid, minecraftServer);

                            var otherPlayerPos = otherPlayer.position();
                            var distance = playerPos.distanceTo(otherPlayerPos);

                            // Linear offset reduction based on how far you are from player.
                            otherData.eventTimestamp += (long) (((double) randomOffset / GROUP_DISTANCE) * (GROUP_DISTANCE - distance));
                            otherData.eventUnlockTimestamp = currentTime + randomLock;
                        }
                    }
                } else {
                    // TODO: Randomize delay with config
                    var randomDelay = 500;
                    data.eventTimestamp = currentTime + randomDelay;
                }
            }
        }


        // Process global random events
        if (timeToGlobalEvent <= 0) {
            var cause = new GlobalRandomEventCause();

            var random = E418Random.EVENT_ENGINE_PLAYER;

            var success = false;
            var ctx = new EventContext(minecraftServer)
                    .withCause(cause);
            var e = EventRegistries.getQueueableEventsWithTag("random.global", ctx).getRandomElement(random);

            if (e != null) {
                success = E418.getEventManager().queueEvent(e, ctx);
            }

            // TODO: Put these values to config
            if (success) {
                timeToGlobalEvent = random.nextInt(2400, 4800);
            } else {
                timeToGlobalEvent = random.nextInt(1200, 2400);
            }
        }
    }
}
