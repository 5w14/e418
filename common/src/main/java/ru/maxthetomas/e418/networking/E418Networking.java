package ru.maxthetomas.e418.networking;

import dev.architectury.networking.NetworkManager;

public class E418Networking {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CShowToast.TYPE, S2CShowToast.STREAM_CODEC, S2CShowToast::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSetShader.TYPE, S2CSetShader.STREAM_CODEC, S2CSetShader::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSetSnowRender.TYPE, S2CSetSnowRender.STREAM_CODEC, S2CSetSnowRender::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CCrashGame.TYPE, S2CCrashGame.STREAM_CODEC, S2CCrashGame::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSetBreakAtlas.TYPE, S2CSetBreakAtlas.STREAM_CODEC, S2CSetBreakAtlas::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSetMetaParanoia.TYPE, S2CSetMetaParanoia.STREAM_CODEC, S2CSetMetaParanoia::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSetSun.TYPE, S2CSetSun.STREAM_CODEC, S2CSetSun::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSetMoon.TYPE, S2CSetMoon.STREAM_CODEC, S2CSetMoon::receive);
    }
}
