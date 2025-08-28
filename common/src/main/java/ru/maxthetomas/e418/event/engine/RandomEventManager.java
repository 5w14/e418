package ru.maxthetomas.e418.event.engine;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.GlobalRandomEventCause;
import ru.maxthetomas.e418.event.cause.impl.PlayerRandomEventCause;
import ru.maxthetomas.e418.event.registry.EventRegistries;
import ru.maxthetomas.e418.player.PlayerDataManager;
import ru.maxthetomas.e418.util.E418Random;
import ru.maxthetomas.e418.util.Location;

public class RandomEventManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public long GlobalEventTick = -1L;

    public RandomEventManager() {
        TickEvent.SERVER_POST.register(this::tick);
        LifecycleEvent.SERVER_STARTED.register(this::serverStarted);
        // TODO: Load time from save data
    }

    public long getGlobalEventTick() {
        return GlobalEventTick;
    }

    private void serverStarted(MinecraftServer minecraftServer) {
        reset(minecraftServer);
    }

    public void reset(MinecraftServer srv) {
        GlobalEventTick = Config.globalRandomEventGracePeriod.get().randomValue(E418Random.EVENT_ENGINE_GLOBAL) + srv.overworld().getGameTime();
    }

    public void tick(MinecraftServer minecraftServer) {
        var currentTime = minecraftServer.overworld().getGameTime();

        if (minecraftServer.getPlayerCount() == 0)
            return;

        // Process player random events
        for (ServerPlayer player : minecraftServer.getPlayerList().getPlayers()) {
            var uuid = player.getUUID();
            var data = PlayerDataManager.ensureData(uuid, minecraftServer);

            if (data.eventTimestamp < currentTime) {
                var random = E418Random.EVENT_ENGINE_GLOBAL;
                // Start event

                var range = Config.playerRandomEventGroupDistance.get();

                // Get nearby players
                var playersInRange = minecraftServer.getPlayerList().getPlayers().stream().filter((p) -> {
                    return (p != player &&
                            p.level() == player.level() &&
                            player.position().closerThan(p.position(), range));
                }).toList();

                var hasLocks = playersInRange.stream().anyMatch((p) -> {
                    var pdata = PlayerDataManager.ensureData(p.getUUID(), minecraftServer);

                    return pdata.eventUnlockTimestamp > currentTime;
                });

                // We prevent event start if there's a player with lock nearby.
                if (hasLocks) {
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
                    var randomDelay = Config.playerRandomEventDelay.get().randomValue(random);
                    var randomOffset = Config.playerRandomEventOffset.get().randomValue(random);
                    var randomLock = Config.playerRandomEventLock.get().randomValue(random);

                    data.eventTimestamp = currentTime + randomDelay + randomOffset;
                    data.eventUnlockTimestamp = currentTime + randomLock;

                    // Delay event time for players nearby
                    if (success && !cause.isGroupEffectCancelled() && range > 0) {
                        var playerPos = player.position();

                        for (ServerPlayer otherPlayer : playersInRange) {
                            var otherUuid = otherPlayer.getUUID();
                            var otherData = PlayerDataManager.ensureData(otherUuid, minecraftServer);

                            var otherPlayerPos = otherPlayer.position();
                            var distance = playerPos.distanceTo(otherPlayerPos);

                            // Linear offset reduction based on how far you are from player.
                            otherData.eventTimestamp += (long) (((double) randomOffset / range) * (range - distance));
                            otherData.eventUnlockTimestamp = currentTime + randomLock;
                        }
                    }
                } else {
                    var randomDelay = Config.playerRandomEventDelayFailure.get().randomValue(random);
                    data.eventTimestamp = currentTime + randomDelay;
                }
            }
        }


        // Process global random events
        if (GlobalEventTick <= currentTime) {
            var cause = new GlobalRandomEventCause();

            var random = E418Random.EVENT_ENGINE_PLAYER;

            var success = false;
            var ctx = new EventContext(minecraftServer)
                    .withCause(cause);
            var e = EventRegistries.getQueueableEventsWithTag("random.global", ctx).getRandomElement(random);

            if (e != null) {
                success = E418.getEventManager().queueEvent(e, ctx);
            }

            if (success) {
                GlobalEventTick = currentTime + Config.globalRandomEventDelay.get().randomValue(random);
            } else {
                GlobalEventTick = currentTime + Config.globalRandomEventDelayFailure.get().randomValue(random);
            }
        }
    }
}
