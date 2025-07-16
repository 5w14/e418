package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/// Returns true only when in certain biome
///
/// <li> <code>biome</code> - Biome to trigger
public class InBiomeCondition implements ICondition {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "in_biome");
    public static MapCodec<InBiomeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("biome").forGetter(InBiomeCondition::getBiome)
    ).apply(instance, InBiomeCondition::new));

    private final ResourceLocation biome;

    public InBiomeCondition(ResourceLocation biome) {
        this.biome = biome;
    }

    @Override
    public boolean check(EventContext context) {
        var level = context.getPlayer().level();
        var player = context.getPlayer();

        return level.getBiome(player.getOnPos()).is(biome);
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public ResourceLocation getBiome() {
        return biome;
    }
}
