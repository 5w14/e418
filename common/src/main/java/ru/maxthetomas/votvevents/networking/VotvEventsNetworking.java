package ru.maxthetomas.votvevents.networking;

import dev.architectury.networking.NetworkManager;

public class VotvEventsNetworking {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CShowToast.TYPE,
                S2CShowToast.STREAM_CODEC, S2CShowToast::receive);
    }
}
