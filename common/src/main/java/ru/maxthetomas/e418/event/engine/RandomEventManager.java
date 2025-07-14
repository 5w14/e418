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
    private static final int TIME_OFFSET_PER_BLOCK = 900;

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

                // TODO: Randomize delay
                var randomDelay = 600;
                var randomOffset = 600;
                data.eventTimestamp = currentTime + randomDelay + randomOffset;

                var cause = new PlayerRandomEventCause(uuid);
                EventRegistries.PLAYER_RANDOM.eventTick(cause);

                // Delay event time for players nearby
                if (!cause.isGroupEffectCancelled() && GROUP_DISTANCE > 0) {
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
                            var otherData = PlayerDataManager.ensureData(otherUuid, minecraftServer);

                            var distance = playerPos.distanceTo(otherPlayerPos);

                            // Linear offset reduction based on how far you are from player.
                            otherData.eventTimestamp += (long) (((double) randomOffset / GROUP_DISTANCE) * (GROUP_DISTANCE-distance));
                        }
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
