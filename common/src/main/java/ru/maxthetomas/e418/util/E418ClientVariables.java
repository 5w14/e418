package ru.maxthetomas.e418.util;

import net.minecraft.resources.ResourceLocation;

public class E418ClientVariables {
    public static boolean ShouldDisplaySnow;
    public static boolean ShouldBreakAtlas;
    public static boolean ShouldRenderPostEffect;
    public static boolean ShouldHaveMetaParanoia;
    public static ResourceLocation SunResource;
    public static ResourceLocation MoonResource;

    static {
        init();
    }

    public static void init() {
        ShouldDisplaySnow = false;
        ShouldBreakAtlas = false;
        ShouldRenderPostEffect = false;
        ShouldHaveMetaParanoia = false;
        SunResource = ResourceLocation.withDefaultNamespace("empty");
        MoonResource = ResourceLocation.withDefaultNamespace("empty");
    }
}
