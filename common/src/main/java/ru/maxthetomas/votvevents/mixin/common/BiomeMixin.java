package ru.maxthetomas.votvevents.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.maxthetomas.votvevents.util.VotvEventsVariables;

@Mixin(Biome.class)
public class BiomeMixin {
    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;I)Z"),
            method = "shouldSnow")
    public boolean warmToRain(boolean input) {
        if (VotvEventsVariables.ShouldSnow)
            return false;
        return input;
    }

    @Inject(at = @At("RETURN"), method = "hasPrecipitation", cancellable = true)
    public void hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
        if (VotvEventsVariables.ShouldSnow)
            cir.setReturnValue(true);
    }
}
