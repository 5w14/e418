package ru.maxthetomas.votvevents.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "startSleepInBed", at = @At("HEAD"))
    private void onStartSleepInBed(BlockPos arg3, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cl) {
        // TODO: Trigger events when trying to sleep
    }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    private void onStopSleepInBed(boolean bl1, boolean bl2, CallbackInfo cl) {
        if (((Player) (Object) this).getCommandSenderWorld().isClientSide()) {
            return;
        }

        // TODO: Trigger events on wakeup
    }
}
