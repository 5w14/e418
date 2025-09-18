package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when player HP is in range
 * <ul>
 *   <li><code>above</code> - Health above.</li>
 *   <li><code>below</code> - Health below.</li>
 * </ul>
 */
public class HealthCondition implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("health");
    public static final MapCodec<HealthCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("above", -Float.MAX_VALUE).forGetter(HealthCondition::getAbove),
            Codec.FLOAT.optionalFieldOf("below", Float.MAX_VALUE).forGetter(HealthCondition::getBelow)
    ).apply(instance, HealthCondition::new));

    private final float above;
    private final float below;

    public HealthCondition(float above, float below) {
        this.above = above;
        this.below = below;
    }

    @Override
    public boolean check(EventContext context) {
        return context.getPlayer().getHealth() >= above && context.getPlayer().getHealth() <= below;
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
