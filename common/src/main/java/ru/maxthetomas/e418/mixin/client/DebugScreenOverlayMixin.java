package ru.maxthetomas.e418.mixin.client;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.maxthetomas.e418.E418;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    /**
     * Modifies block state for mod's blocks.
     */
    @ModifyVariable(
            method = "getSystemInformation",
            at = @At(value = "LOAD"),
            ordinal = 0
    )
    public BlockState modifyHitBlockState(BlockState value) {
        if (E418.getConfig().isDebug())
            return value;

        var regName = value.getBlock().arch$registryName();
        if (regName != null && regName.getNamespace().equals(E418.MOD_ID))
            return Blocks.AIR.defaultBlockState();

        return value;
    }
}
