package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/// Returns true only when gamble was worth it
///
/// <li> <code>chance</code> - Chance in range (0-1) to trigger
public class RandomCondition implements ICondition {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "random");
    public static final MapCodec<RandomCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.floatRange(0, 1).fieldOf("chance").forGetter(RandomCondition::getChance),
            ResourceLocation.CODEC.optionalFieldOf("randomSequence", null).forGetter(RandomCondition::getRandomSequence)
    ).apply(instance, RandomCondition::new));

    private final float chance;

    @Nullable
    private final ResourceLocation random_sequence;

    public RandomCondition(float chance, @Nullable ResourceLocation randomSequence) {
        this.chance = chance;
        random_sequence = randomSequence;
    }

    @Override
    public boolean check(EventContext context) {
        if (random_sequence != null) {
            var random = context.getServer().overworld().getRandomSequence(random_sequence);
            return random.nextFloat() < chance;
        }

        return context.getServer().overworld().getRandom().nextFloat() < chance;
    }

    @Override
    public ResourceLocation getType() {
        return null;
    }

    public float getChance() {
        return chance;
    }

    @Nullable
    public ResourceLocation getRandomSequence() {
        return random_sequence;
    }
}
