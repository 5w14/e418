package ru.maxthetomas.votvevents.mixin.client;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.votvevents.VotvEvents;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    private boolean effectActive;

    @Inject(method = "togglePostEffect", at = @At("HEAD"), cancellable = true)
    public void togglePostEffectOverride(CallbackInfo ci) {
        if (!VotvEvents.getConfig().get().isDebug()) {
            this.effectActive = true;
            ci.cancel();
        }
    }
}
