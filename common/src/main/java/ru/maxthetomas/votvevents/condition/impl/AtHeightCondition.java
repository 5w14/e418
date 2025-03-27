package ru.maxthetomas.votvevents.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

public class AtHeightCondition implements ICondition {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "at_height");
    public static MapCodec<AtHeightCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("above", Float.MAX_VALUE).forGetter(AtHeightCondition::getAbove),
            Codec.FLOAT.optionalFieldOf("below", Float.MIN_VALUE).forGetter(AtHeightCondition::getBelow)
    ).apply(instance, AtHeightCondition::new));

    private final float above;
    private final float below;

    public AtHeightCondition(float above, float below) {
        this.above = above;
        this.below = below;
    }


    @Override
    public boolean check(EventContext context) {
        var positionY = context.getPlayer().position().y;

        // Check if it's a range
        var has_above = above != Float.MAX_VALUE;
        var has_below = below != Float.MIN_VALUE;

        if (has_above && has_below) {
            return positionY > above && positionY < below;
        } else {
            return positionY > above || positionY < below;
        }
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public float getAbove() {
        return above;
    }

    public float getBelow() {
        return below;
    }
}
