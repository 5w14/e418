package ru.maxthetomas.votvevents.util;

public class VotvEventsClientVariables {
    public static boolean ShouldDisplaySnow;

    static {
        init();
    }

    public static void init() {
        ShouldDisplaySnow = false;
    }
}
