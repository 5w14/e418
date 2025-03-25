package ru.maxthetomas.votvevents;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import ru.maxthetomas.votvevents.event.EventManager;

import java.util.Optional;

public final class VotvEvents {
    public static final String MOD_ID = "votvevents";
    private static MinecraftServer ManagedServer = null;

    private static EventManager EventManager = new EventManager();

    public static void init() {
        LifecycleEvent.SERVER_STARTING.register(srv -> ManagedServer = srv);
        LifecycleEvent.SERVER_STOPPED.register(srv -> {
            if (srv == ManagedServer) {
                ManagedServer = null;
            }
        });

        ReloadListenerRegistry.register(PackType.SERVER_DATA, EventManager, ResourceLocation.tryBuild(MOD_ID, "event_reload_listener"));
    }

    public static Optional<MinecraftServer> getCurrentServer() {
        return Optional.ofNullable(ManagedServer);
    }
}
