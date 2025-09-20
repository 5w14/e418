package ru.maxthetomas.e418.util;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.codecs.FogCodecs;

public class E418ClientVariables {
    public static boolean ShouldDisplaySnow;
    public static boolean ShouldBreakAtlas;
    public static boolean ShouldRenderPostEffect;
    public static boolean ShouldHaveMetaParanoia;
    public static ResourceLocation SunResource;
    public static ResourceLocation MoonResource;
    public static FogCodecs.FogConfig FogParametersOverride;

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
        FogParametersOverride = null;
    }
}
