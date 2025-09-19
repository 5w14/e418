package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.FloatRange;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when in range of time of the day.
 * <ul>
 *   <li><code>min</code> - Minimum time.</li>
 *   <li><code>max</code> - Maximum time.</li>
 * </ul>
 */
public record AtTimeCondition(FloatRange range) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("at_time");
    public static final MapCodec<AtTimeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FloatRange.CODEC.codec().optionalFieldOf("range", new FloatRange(0.0f, 24000.0f)).forGetter(AtTimeCondition::range)
    ).apply(instance, AtTimeCondition::new));

    @Override
    public boolean check(EventContext context) {
        var currentTime = context.getServer().overworld().getDayTime() % 24000;
        return range.isIn(currentTime);
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
