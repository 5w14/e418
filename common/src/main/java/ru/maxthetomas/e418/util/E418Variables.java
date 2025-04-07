package ru.maxthetomas.e418.util;

// todo: Either store this in world, or load from the world / re-execute events or something
public class E418Variables {
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
