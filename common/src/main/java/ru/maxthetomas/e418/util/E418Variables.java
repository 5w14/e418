package ru.maxthetomas.e418.util;

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
