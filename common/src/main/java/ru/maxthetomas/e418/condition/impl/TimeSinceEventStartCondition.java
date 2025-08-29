package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.NumberProvider;
import ru.maxthetomas.e418.codecs.NumberProviders;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when enough ticks have passed.
 * <ul>
 *   <li><code>ticks</code> - Ticks to trigger.</li>
 * </ul>
 */
public record TimeSinceEventStartCondition(NumberProvider ticks) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("time_since_start");
    public static final MapCodec<TimeSinceEventStartCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("ticks").forGetter(TimeSinceEventStartCondition::ticks)
    ).apply(instance, TimeSinceEventStartCondition::new));

    @Override
    public boolean check(EventContext context) {
        return context.getServer().overworld().getGameTime() >
                context.getSourceEvent().startTime + ticks.get(context, this).longValue();
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
