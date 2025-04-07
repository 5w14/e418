package ru.maxthetomas.votvevents.networking;

import dev.architectury.networking.NetworkManager;

public class VotvEventsNetworking {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CShowToast.TYPE,
                S2CShowToast.STREAM_CODEC, S2CShowToast::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSetShader.TYPE,
                S2CSetShader.STREAM_CODEC, S2CSetShader::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSetSnowRender.TYPE,
                S2CSetSnowRender.STREAM_CODEC, S2CSetSnowRender::receive);
    }
}
