package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.FloatRange;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when player HP is in range
 * <ul>
 *   <li><code>min</code> - Health below.</li>
 *   <li><code>max</code> - Health above.</li>
 * </ul>
 */
public record HealthCondition(FloatRange range) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("health");
    public static final MapCodec<HealthCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FloatRange.CODEC.codec().optionalFieldOf("range", new FloatRange(0.0f, 20.0f)).forGetter(HealthCondition::range)
    ).apply(instance, HealthCondition::new));

    @Override
    public boolean check(EventContext context) {
        return range.isIn(context.getPlayer().getHealth());
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
