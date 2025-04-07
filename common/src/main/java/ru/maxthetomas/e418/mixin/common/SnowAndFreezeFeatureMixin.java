package ru.maxthetomas.e418.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.maxthetomas.e418.util.E418Variables;

@Mixin(SnowAndFreezeFeature.class)
public class SnowAndFreezeFeatureMixin {
    @ModifyExpressionValue(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"))
    public boolean shouldSnow(boolean original, FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        if (!E418Variables.ShouldSnow)
            return original;

        // Another check instead of the overridden one.

        var biome = featurePlaceContext.level().getBiome(featurePlaceContext.origin()).value();

        if (biome.getBaseTemperature() > 0.15f)
            return false;

        return original;
    }
}
