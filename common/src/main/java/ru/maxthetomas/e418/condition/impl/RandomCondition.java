package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

public class RandomCondition implements ICondition {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "random");
    public static final MapCodec<RandomCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.floatRange(0, 1).fieldOf("chance").forGetter(RandomCondition::getChance)
    ).apply(instance, RandomCondition::new));

    private final float chance;

    public RandomCondition(float chance) {
        this.chance = chance;
    }

    @Override
    public boolean check(EventContext context) {
        return context.getServer().overworld().getRandom().nextFloat() < chance;
    }

    @Override
    public ResourceLocation getType() {
        return null;
    }

    public float getChance() {
        return chance;
    }
}
