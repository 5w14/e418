package ru.maxthetomas.votvevents.util;

// todo: Either store this in world, or load from the world / re-execute events or something
public class VotvEventsVariables {
    public static boolean DisableNightSkip;
    public static boolean ShouldSnow;

    static {
        init();
    }

    public static void init() {
        ShouldSnow = false;
        DisableNightSkip = false;
    }
}
