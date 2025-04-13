package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

public class AtTimeCondition implements ICondition {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "at_time");
    public static MapCodec<AtTimeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("from", 0).forGetter(AtTimeCondition::getFrom),
            Codec.INT.optionalFieldOf("to", 0).forGetter(AtTimeCondition::getTo)
    ).apply(instance, AtTimeCondition::new));

    private int from;
    private int to;

    public AtTimeCondition(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean check(EventContext context) {
        var currentTime = context.getServer().overworld().getDayTime() % 24000;
        return currentTime >= from && currentTime <= to;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
