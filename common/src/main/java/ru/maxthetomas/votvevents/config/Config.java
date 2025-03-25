package ru.maxthetomas.votvevents.config;

import dev.architectury.platform.Platform;

public class Config {
    private static final boolean IS_DEBUG = false;

    public static boolean IsDebug() {
        return Platform.isDevelopmentEnvironment() || IS_DEBUG;
    }
}