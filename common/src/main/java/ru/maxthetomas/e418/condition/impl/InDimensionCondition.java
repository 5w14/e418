package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when player is in specific dimension
 */
public record InDimensionCondition(ResourceLocation dimension) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("in_dimension");
    public static final MapCodec<InDimensionCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(InDimensionCondition::dimension)
    ).apply(instance, InDimensionCondition::new));

    @Override
    public boolean check(EventContext context) {
        return context.getPlayer().level().dimension().location().equals(dimension);
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
