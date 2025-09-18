package ru.maxthetomas.e418.fabric;

import net.fabricmc.api.ModInitializer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.fabric.storage.AttachmentTypes;
import ru.maxthetomas.e418.fabric.storage.FabricPlatformData;
import ru.maxthetomas.e418.util.storage.PlatformDataManager;

public final class E418Fabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        E418.init();

        PlatformDataManager.PLAYER_DATA = new FabricPlatformData<>(AttachmentTypes.PLAYER_DATA);
        PlatformDataManager.CHUNK_DATA = new FabricPlatformData<>(AttachmentTypes.CHUNK_DATA);
    }
}
