package ru.maxthetomas.votvevents.mixin.common;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.maxthetomas.votvevents.VotvEvents;

@Mixin(StructureManager.class)
public class StructureManagerMixin {
    @Shadow
    @Final
    private LevelAccessor level;

    @Inject(at = @At("RETURN"), cancellable = true, method = "shouldGenerateStructures")
    public void override(CallbackInfoReturnable<Boolean> cir) {
        if (this.level.isClientSide()) return;
        var worldGen = (WorldGenRegion) this.level;
        @SuppressWarnings("deprecation") var sLevel = worldGen.getLevel();
        var key = sLevel.dimension().location();
        if (key.equals(VotvEvents.NO_ENTITY_AND_STRUCTURE_DIMENSION_ID))
            cir.setReturnValue(false);
    }
}
