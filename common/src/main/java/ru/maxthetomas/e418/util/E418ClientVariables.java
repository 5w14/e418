package ru.maxthetomas.e418.util;

public class E418ClientVariables {
    public static boolean ShouldDisplaySnow;
    public static boolean ShouldBreakAtlas;

    static {
        init();
    }

    public static void init() {
        ShouldDisplaySnow = false;
        ShouldBreakAtlas = false;
    }
}
