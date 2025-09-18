package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.FloatRange;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * The condition that succeeds only if the player is in the range
 * <li><code>min</code> - Minimum height.</li>
 * <li><code>max</code> - Maximum height.</li>
 */
public record AtHeightCondition(FloatRange range) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("at_height");
    public static final MapCodec<AtHeightCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FloatRange.CODEC.codec().optionalFieldOf("range", new FloatRange(-64, 320)).forGetter(AtHeightCondition::range)
    ).apply(instance, AtHeightCondition::new));

    /**
     * Creates a new instance of AtHeightCondition.
     */
    public AtHeightCondition {
    }


    @Override
    public boolean check(EventContext context) {
        var positionY = context.getPlayer().position().y;

        if (range.max() < range.min()) {
            return !range.isIn((float) positionY);
        }
        return range.isIn((float) positionY);
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}