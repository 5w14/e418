package ru.maxthetomas.votvevents.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.votvevents.VotvEvents;

@Mixin(LivingEntity.class)
public class EntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide || self instanceof Player)
            return;

        if (self.level().dimension().location().equals(VotvEvents.NO_ENTITY_AND_STRUCTURE_DIMENSION_ID))
            self.remove(Entity.RemovalReason.DISCARDED);
    }
}
