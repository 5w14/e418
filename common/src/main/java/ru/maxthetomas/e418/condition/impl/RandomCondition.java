package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.util.E418Random;

/**
 * Returns true only when the gamble was worth it.
 * <ul>
 *   <li><code>chance</code> - Chance in range (0-1) to trigger.</li>
 * </ul>
 */
public record RandomCondition(float chance, ResourceLocation randomSequence) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("random");
    public static final MapCodec<RandomCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.floatRange(0, 1).fieldOf("chance").forGetter(RandomCondition::chance),
            ResourceLocation.CODEC.optionalFieldOf("randomSequence", null).forGetter(RandomCondition::randomSequence)
    ).apply(instance, RandomCondition::new));

    @Override
    public boolean check(EventContext context) {
        if (randomSequence != null) {
            var random = context.getServer().overworld().getRandomSequence(randomSequence);
            return random.nextFloat() < chance;
        }

        return E418Random.EVENT_GENERIC.nextFloat() < chance;
    }

    @Override
    public ResourceLocation getType() {
        return null;
    }
}
