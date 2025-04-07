package ru.maxthetomas.e418.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.maxthetomas.e418.util.E418ClientVariables;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {
//    @ModifyExpressionValue(method = "getPrecipitationAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
//    public Biome.Precipitation getPrecipitationAt(Biome.Precipitation original) {
//        return Biome.Precipitation.SNOW;
//    }

    @ModifyReturnValue(method = "getPrecipitationAt", at = @At("RETURN"))
    public Biome.Precipitation a(Biome.Precipitation original) {
        if (E418ClientVariables.ShouldDisplaySnow)
            return Biome.Precipitation.SNOW;

        return original;
    }
}
