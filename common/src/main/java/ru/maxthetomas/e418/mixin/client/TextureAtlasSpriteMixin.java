package ru.maxthetomas.e418.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.maxthetomas.e418.util.E418ClientVariables;

import java.util.Map;

@Mixin(TextureAtlas.class)
public class TextureAtlasSpriteMixin {
    @Shadow
    private Map<ResourceLocation, TextureAtlasSprite> texturesByName;
    @Shadow
    private @Nullable TextureAtlasSprite missingSprite;

    @ModifyReturnValue(method = "getSprite", at = @At("RETURN"))
    public TextureAtlasSprite getSprite(TextureAtlasSprite original) {
        if (!E418ClientVariables.ShouldBreakAtlas)
            return original;

        var random = RandomSource.create(original.hashCode() ^ (long) this.hashCode() << 3 ^ 0x2485);
        var keyset = texturesByName.keySet().toArray();
        return texturesByName.getOrDefault(keyset[random.nextInt(keyset.length)], this.missingSprite);
    }
}
