package ru.maxthetomas.votvevents.mixin.client;


import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.votvevents.VotvEvents;

@Mixin(WorldOpenFlows.class)
public abstract class WorldOpenFlowsMixin {
    @Shadow
    protected abstract void openWorldLoadBundledResourcePack(LevelStorageSource.LevelStorageAccess levelStorageAccess, WorldStem worldStem, PackRepository packRepository, Runnable runnable);

    @Inject(at = @At("HEAD"), method = "openWorldCheckWorldStemCompatibility", cancellable = true)
    public void modifyAllowsForBackupSkin(LevelStorageSource.LevelStorageAccess levelStorageAccess, WorldStem worldStem, PackRepository packRepository, Runnable runnable, CallbackInfo ci) {
        if (VotvEvents.getConfig().get().shouldSkipBackupScreen()) {
            // This skips the method execution and mimics
            // it's function without backup checks.

            ci.cancel();
            this.openWorldLoadBundledResourcePack(levelStorageAccess, worldStem, packRepository, runnable);
        }
    }
}
