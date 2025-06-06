package ru.maxthetomas.e418;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.GameInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.debug.EventCommand;
import ru.maxthetomas.e418.event.EventManager;
import ru.maxthetomas.e418.event.RandomEventManager;
import ru.maxthetomas.e418.networking.E418Networking;
import ru.maxthetomas.e418.util.E418ClientVariables;
import ru.maxthetomas.e418.util.E418Variables;

import java.util.Optional;

public final class E418 {
    public static final String MOD_ID = "e418";
    private static final Config ModConfig = Config.loadConfig();
    private static final EventManager EventManager = new EventManager();
    private static final RandomEventManager RandomEventManager = new RandomEventManager();
    private static MinecraftServer ManagedServer = null;

    public static void init() {
        E418Networking.init();
        CommandRegistrationEvent.EVENT.register(EventCommand::register);
        ReloadListenerRegistry.register(PackType.SERVER_DATA, EventManager, ResourceLocation.tryBuild(MOD_ID, "event_reload_listener"));
        registerListeners();
        Config.saveToFile(ModConfig);
    }

    private static void registerListeners() {
        LifecycleEvent.SERVER_BEFORE_START.register(srv -> {
            ManagedServer = srv;
            E418Variables.init();
        });

        LifecycleEvent.SERVER_STOPPING.register(srv -> {
            if (srv == ManagedServer) {
                EventManager.getActiveEvents().forEach(EventManager::disposeEvent);
            }
        });

        LifecycleEvent.SERVER_STOPPED.register(srv -> {
            if (srv == ManagedServer) {
                ManagedServer = null;
            }
            E418Variables.init();
        });

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(evt -> {
            E418ClientVariables.init();
        });
    }

    public static EventManager getEventManager() {
        return EventManager;
    }

    public static Optional<MinecraftServer> getCurrentServer() {
        return Optional.ofNullable(GameInstance.getServer());
    }

    public static Config getConfig() {
        return ModConfig;
    }

    public static RandomEventManager getRandomEventManager() {
        return RandomEventManager;
    }
}
