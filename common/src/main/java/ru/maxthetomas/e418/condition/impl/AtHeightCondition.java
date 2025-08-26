package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * The condition that succeeds only if the player is either:
 *
 * <ul>
 *     <li>If only <code>above</code> is parsed - above the height.</li>
 *     <li>If only <code>below</code> is parsed - below the height.</li>
 *     <li>If both <code>above</code> and <code>below</code> is parsed - in range between <code>above</code> and <code>below</code>.</li>
 * </ul>
 */
public class AtHeightCondition implements ICondition {
    public static ResourceLocation ID = E418.resLoc("at_height");
    public static MapCodec<AtHeightCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("above", Float.MAX_VALUE).forGetter(AtHeightCondition::getAbove),
            Codec.FLOAT.optionalFieldOf("below", -Float.MAX_VALUE).forGetter(AtHeightCondition::getBelow)
    ).apply(instance, AtHeightCondition::new));

    private final float above;
    private final float below;

    /**
     * Creates a new instance of AtHeightCondition.
     *
     * @param above Check that the player is above this height. Use <code>Float.MAX_VALUE</code> to disable.
     * @param below Check that the player is below this height. Use <code>-Float.MAX_VALUE</code> to disable.
     */
    public AtHeightCondition(float above, float below) {
        this.above = above;
        this.below = below;
    }


    @Override
    public boolean check(EventContext context) {
        var positionY = context.getPlayer().position().y;

        // Check if it's a range
        var has_above = above != Float.MAX_VALUE;
        var has_below = below != -Float.MAX_VALUE;

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