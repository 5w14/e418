package ru.maxthetomas.e418.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.maxthetomas.e418.util.E418Variables;

@Mixin(Biome.class)
public class BiomeMixin {
    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;I)Z"),
            method = "shouldSnow")
    public boolean warmToRain(boolean input) {
        if (E418Variables.ShouldSnow)
            return false;
        return input;
    }

    @Inject(at = @At("RETURN"), method = "hasPrecipitation", cancellable = true)
    public void hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
        if (E418Variables.ShouldSnow)
            cir.setReturnValue(true);
    }
}
