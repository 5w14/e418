package ru.maxthetomas.votvevents.config;

import dev.architectury.platform.Platform;

public class Config {
    private static final boolean _isDebug = false;

    public static boolean IsDebug() {
        return Platform.isDevelopmentEnvironment() || _isDebug;
    }
}