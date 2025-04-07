package ru.maxthetomas.e418.fabric;

import net.fabricmc.api.ModInitializer;
import ru.maxthetomas.e418.E418;

public final class E418Fabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        E418.init();
    }
}
