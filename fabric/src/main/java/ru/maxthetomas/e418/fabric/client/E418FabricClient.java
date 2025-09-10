package ru.maxthetomas.e418.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import ru.maxthetomas.e418.E418Client;

public final class E418FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        E418Client.init();
    }
}
