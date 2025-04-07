package ru.maxthetomas.votvevents.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.maxthetomas.votvevents.util.VotvEventsClientVariables;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {
//    @ModifyExpressionValue(method = "getPrecipitationAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
//    public Biome.Precipitation getPrecipitationAt(Biome.Precipitation original) {
//        return Biome.Precipitation.SNOW;
//    }

    @ModifyReturnValue(method = "getPrecipitationAt", at = @At("RETURN"))
    public Biome.Precipitation a(Biome.Precipitation original) {
        if (VotvEventsClientVariables.ShouldDisplaySnow)
            return Biome.Precipitation.SNOW;

        return original;
    }
}
