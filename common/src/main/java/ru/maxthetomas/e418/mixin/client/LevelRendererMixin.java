package ru.maxthetomas.e418.mixin.client;

import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @ModifyVariable(
            at = @At("LOAD"),
            method = "renderLevel"
    )
    public Vector4f modifyFogColor(Vector4f vector4f) {
        return vector4f;
    }

    @ModifyVariable(
            at = @At("LOAD"),
            method = "renderLevel",
            ordinal = 0
    )
    public FogParameters modifyTerrainFogSettings(FogParameters fogParameters) {
        return fogParameters;
    }

    @ModifyVariable(
            at = @At("LOAD"),
            method = "renderLevel",
            ordinal = 1
    )
    public FogParameters modifySkyFogSettings(FogParameters fogParameters) {
        return fogParameters;
    }
}
