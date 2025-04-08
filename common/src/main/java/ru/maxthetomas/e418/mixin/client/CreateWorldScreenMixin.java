package ru.maxthetomas.e418.mixin.client;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.maxthetomas.e418.E418;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @ModifyArg(
            method = "onCreate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;confirmWorldCreation(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;Lcom/mojang/serialization/Lifecycle;Ljava/lang/Runnable;Z)V")
    )
    public boolean modifySkip(boolean bool) {
        if (E418.getConfig().shouldSkipBackupScreen()) {
            return true;
        }

        return bool;
    }
}
