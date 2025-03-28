package ru.maxthetomas.votvevents;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import ru.maxthetomas.votvevents.config.Config;
import ru.maxthetomas.votvevents.debug.EventCommand;
import ru.maxthetomas.votvevents.event.EventManager;
import ru.maxthetomas.votvevents.event.RandomEventManager;

import java.util.Optional;

public final class VotvEvents {
    public static final String MOD_ID = "votvevents";
    private static MinecraftServer ManagedServer = null;

    private static EventManager EventManager = new EventManager();
    private static Config ModConfig = Config.getConfig();

    public static void init() {
        LifecycleEvent.SERVER_BEFORE_START.register(srv -> ManagedServer = srv);
        LifecycleEvent.SERVER_STOPPED.register(srv -> {
            if (srv == ManagedServer) {
                ManagedServer = null;
            }
        });

        RandomEventManager.init();

        CommandRegistrationEvent.EVENT.register(EventCommand::register);

        ReloadListenerRegistry.register(PackType.SERVER_DATA, EventManager, ResourceLocation.tryBuild(MOD_ID, "event_reload_listener"));
    }

    public static EventManager getEventManager() {
        return EventManager;
    }

    public static Optional<MinecraftServer> getCurrentServer() {
        return Optional.ofNullable(ManagedServer);
    }

    public static Optional<Config> getConfig() {
        return Optional.ofNullable(ModConfig);
    }
}
