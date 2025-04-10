package ru.maxthetomas.e418.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.maxthetomas.e418.util.E418ClientVariables;

import java.util.Map;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
    @Shadow
    @Final
    private Map<ResourceLocation, AbstractTexture> byPath;

    @ModifyReturnValue(method = "getTexture", at = @At("RETURN"))
    AbstractTexture getTexture(AbstractTexture original) {
        if (!E418ClientVariables.ShouldBreakAtlas) {
            return original;
        }

        var random = RandomSource.create((long) original.hashCode() << 11 ^ 0x24261);
        var keys = this.byPath.keySet().toArray();
        return this.byPath.get(keys[random.nextInt(keys.length)]);
    }
}
