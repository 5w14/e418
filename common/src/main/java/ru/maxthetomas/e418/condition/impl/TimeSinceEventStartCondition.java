package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

public class TimeSinceEventStartCondition implements ICondition {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "time_since_start");
    public static MapCodec<TimeSinceEventStartCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("ticks").forGetter(TimeSinceEventStartCondition::getTicks)
    ).apply(instance, TimeSinceEventStartCondition::new));

    private final int ticks;

    public TimeSinceEventStartCondition(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }

    @Override
    public boolean check(EventContext context) {
        return context.getServer().overworld().getGameTime() >
                context.getSourceEvent().startTime + ticks;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
