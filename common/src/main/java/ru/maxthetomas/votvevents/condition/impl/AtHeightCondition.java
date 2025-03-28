package ru.maxthetomas.votvevents.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

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
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "at_height");
    public static MapCodec<AtHeightCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("above", Float.MAX_VALUE).forGetter(AtHeightCondition::getAbove),
            Codec.FLOAT.optionalFieldOf("below", -Float.MAX_VALUE).forGetter(AtHeightCondition::getBelow),
            Codec.BOOL.optionalFieldOf("need_both", true).forGetter(AtHeightCondition::needBoth)
    ).apply(instance, AtHeightCondition::new));

    private final float above;
    private final float below;
    private final boolean needBoth;

    /**
     * Creates a new instance of AtHeightCondition.
     *
     * @param above Check that the player is above this height. Use <code>Float.MAX_VALUE</code> to disable.
     * @param below Check that the player is below this height. Use <code>-Float.MAX_VALUE</code> to disable.
     */
    public AtHeightCondition(float above, float below, boolean needBoth) {
        this.above = above;
        this.below = below;
        this.needBoth = needBoth;
    }


    @Override
    public boolean check(EventContext context) {
        if (context.getPlayer() == null) {
            return false;
        }

        var positionY = context.getPlayer().position().y;

        if (needBoth) {
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

    public boolean needBoth() {
        return needBoth;
    }
}
