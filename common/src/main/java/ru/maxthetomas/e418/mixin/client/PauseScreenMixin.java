package ru.maxthetomas.e418.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.e418.util.E418ClientVariables;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    @Shadow
    @Nullable
    private Button disconnectButton;

    @Inject(method = "createPauseMenu", at = @At("TAIL"))
    private void createPauseMenu(CallbackInfo ci) {
        if (E418ClientVariables.ShouldHaveMetaParanoia && disconnectButton != null) {
            disconnectButton.visible = false;
            disconnectButton.active = false;
        }
    }
}
