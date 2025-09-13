package ru.maxthetomas.e418;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.GameInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.config.ConfigLoader;
import ru.maxthetomas.e418.debug.EventCommand;
import ru.maxthetomas.e418.event.EventManager;
import ru.maxthetomas.e418.event.engine.EventEngine;
import ru.maxthetomas.e418.networking.E418Networking;
import ru.maxthetomas.e418.player.IPlayerDataManager;
import ru.maxthetomas.e418.system.TemporalShiftSystem;
import ru.maxthetomas.e418.util.E418ClientVariables;
import ru.maxthetomas.e418.util.E418Random;
import ru.maxthetomas.e418.util.E418Variables;
import ru.maxthetomas.e418.util.storage.InGameStorage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class E418 {
    public static final String MOD_ID = "e418";
    private static final EventManager EventManager = new EventManager();
    private static final EventEngine EventEngine = new EventEngine();
    public static IPlayerDataManager PlayerDataManager;

    private static MinecraftServer ManagedServer = null;

    public static void init() {
        ConfigLoader.loadConfig();

        E418Networking.init();
        CommandRegistrationEvent.EVENT.register(EventCommand::register);
        ReloadListenerRegistry.register(PackType.SERVER_DATA, EventManager, ResourceLocation.tryBuild(MOD_ID, "event_reload_listener"));
        registerListeners();

        TemporalShiftSystem.init();

        ConfigLoader.saveConfig();
    }

    private static void registerListeners() {
        LifecycleEvent.SERVER_BEFORE_START.register(srv -> {
            EventManager.init();
            EventManager.getActiveEvents().forEach(EventManager::disposeEvent);
            E418Variables.init();
            ManagedServer = srv;
        });

        LifecycleEvent.SERVER_LEVEL_LOAD.register(lvl -> {
            if (lvl.equals(ManagedServer.overworld())) {
                E418Random.init(lvl);
            }
        });

        LifecycleEvent.SERVER_STARTED.register(InGameStorage::load);

        LifecycleEvent.SERVER_LEVEL_SAVE.register(savedWorld -> {
            if (!savedWorld.equals(savedWorld.getServer().overworld()))
                return;
            InGameStorage.INSTANCE.dumpEventManager(getEventManager());
        });

        LifecycleEvent.SERVER_STOPPING.register(srv -> {
            InGameStorage.INSTANCE.dumpEventManager(getEventManager());
            getEventManager().fullReset(srv);
        });

        LifecycleEvent.SERVER_STOPPED.register(srv -> {
            if (srv.equals(ManagedServer)) {
                ManagedServer = null;
            }

            E418Variables.init();
        });
    }

    public static EventManager getEventManager() {
        return EventManager;
    }

    public static EventEngine getEventEngine() {
        return EventEngine;
    }

    public static Optional<MinecraftServer> getCurrentServer() {
        return Optional.ofNullable(GameInstance.getServer());
    }

    public static List<ServerPlayer> allPlayers() {
        if (getCurrentServer().isEmpty()) return List.of();
        return getCurrentServer().get().getPlayerList().getPlayers();
    }

    public static @Nullable ServerPlayer player(UUID uuid) {
        if (getCurrentServer().isEmpty()) return null;
        return getCurrentServer().get().getPlayerList().getPlayer(uuid);
    }

    /**
     * For internal usage only.
     */
    public static ResourceLocation resLoc(String value) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, value);
    }
}
