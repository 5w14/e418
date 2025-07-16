package ru.maxthetomas.e418.mixin.client;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.util.E418ClientVariables;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    private boolean effectActive;

    @Inject(method = "togglePostEffect", at = @At("HEAD"), cancellable = true)
    public void togglePostEffectOverride(CallbackInfo ci) {
        if (!Config.isDebug()) {
            this.effectActive = true;
            ci.cancel();
        }
    }

    @Inject(method = "clearPostEffect", at = @At("HEAD"), cancellable = true)
    public void clearPostEffect(CallbackInfo ci) {
        if (E418ClientVariables.ShouldRenderPostEffect)
            ci.cancel();
    }

    @Inject(method = "checkEntityPostEffect", at = @At("HEAD"), cancellable = true)
    public void checkPostEffect(CallbackInfo ci) {
        if (E418ClientVariables.ShouldRenderPostEffect)
            ci.cancel();
    }
}
