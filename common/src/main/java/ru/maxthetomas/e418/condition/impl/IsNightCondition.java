package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when it is nighttime in the overworld.
 */
public record IsNightCondition() implements ICondition {
    public static final MapCodec<IsNightCondition> CODEC = MapCodec.unit(IsNightCondition::new);
    public static final ResourceLocation ID = E418.resLoc("is_night");

    @Override
    public boolean check(EventContext context) {
        return context.getServer().overworld().isNight();
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
