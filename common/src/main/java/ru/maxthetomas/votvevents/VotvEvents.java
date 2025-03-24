package ru.maxthetomas.votvevents;

import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import ru.maxthetomas.votvevents.event.EventManager;

public final class VotvEvents {
    public static final String MOD_ID = "votvevents";

    private static EventManager EventManager = new EventManager();

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, EventManager, ResourceLocation.tryBuild(MOD_ID, "event_reload_listener"));
    }
}
