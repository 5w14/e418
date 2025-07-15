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
import ru.maxthetomas.e418.player.PlayerDataManager;

import java.util.*;

public class RandomEventManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Config CONFIG = E418.getConfig();
    private static final Random RANDOM = new Random();

    // TODO: Put this into config
    private static final int GROUP_DISTANCE = 50;
    private static final int LOCK_DURATION = 2400;

    private static int timeToGlobalEvent = 20 * 60 * 30;

    public RandomEventManager() {
        TickEvent.SERVER_POST.register(this::tick);
        refreshTimer();
    }

    private static void refreshTimer() {
        var config = EventRegistries.GLOBAL_RANDOM.getConfig();
        timeToGlobalEvent = config.getMinTime() + RANDOM.nextInt(config.getMaxTime() - config.getMinTime());
    }

    public static int getTimeToGlobalEvent(){
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
                    var randomDelay = 600;
                    data.eventTimestamp = currentTime + randomDelay;
                    continue;
                }

                // TODO: Randomize delay with config
                var randomDelay = 600;
                var randomOffset = 600;
                data.eventTimestamp = currentTime + randomDelay + randomOffset;
                data.eventUnlockTimestamp = currentTime + LOCK_DURATION;

                var cause = new PlayerRandomEventCause(uuid);
                EventRegistries.PLAYER_RANDOM.eventTick(cause);

                // Delay event time for players nearby
                if (!cause.isGroupEffectCancelled() && GROUP_DISTANCE > 0) {
                    var playerPos = player.position();

                    for (ServerPlayer otherPlayer : playersInRange) {
                        var otherUuid = otherPlayer.getUUID();
                        var otherData = PlayerDataManager.ensureData(otherUuid, minecraftServer);

                        var otherPlayerPos = otherPlayer.position();
                        var distance = playerPos.distanceTo(otherPlayerPos);

                        // Linear offset reduction based on how far you are from player.
                        otherData.eventTimestamp += (long) (((double) randomOffset / GROUP_DISTANCE) * (GROUP_DISTANCE-distance));
                        otherData.eventUnlockTimestamp = currentTime + LOCK_DURATION;
                    }
                }

            }
        }


        // Process global random events
        if (timeToGlobalEvent <= 0) {
            var cause = new GlobalRandomEventCause();
            EventRegistries.GLOBAL_RANDOM.eventTick(cause);
            refreshTimer();
        }
    }
}
