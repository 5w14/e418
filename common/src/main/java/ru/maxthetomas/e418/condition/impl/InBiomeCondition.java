package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when in a certain biome.
 * <ul>
 *   <li><code>biome</code> - Biome to trigger.</li>
 * </ul>
 */
public record InBiomeCondition(ResourceLocation biome) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("in_biome");
    public static final MapCodec<InBiomeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("biome").forGetter(InBiomeCondition::biome)
    ).apply(instance, InBiomeCondition::new));

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
}
