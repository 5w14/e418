package ru.maxthetomas.e418.util;

public class E418ClientVariables {
    public static boolean ShouldDisplaySnow;
    public static boolean ShouldBreakAtlas;
    public static boolean ShouldRenderPostEffect;
    public static boolean ShouldHaveMetaParanoia;

    static {
        init();
    }

    public static void init() {
        ShouldDisplaySnow = false;
        ShouldBreakAtlas = false;
        ShouldRenderPostEffect = false;
        ShouldHaveMetaParanoia = false;
    }
}
