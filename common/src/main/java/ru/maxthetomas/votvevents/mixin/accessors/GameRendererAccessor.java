package ru.maxthetomas.votvevents.mixin.accessors;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Invoker
    public void callSetPostEffect(ResourceLocation location);

}
