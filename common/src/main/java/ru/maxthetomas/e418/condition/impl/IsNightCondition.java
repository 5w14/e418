package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * A condition that succeeds only if it is the nighttime in the overworld.
 */
public class IsNightCondition implements ICondition {
    public static final MapCodec<IsNightCondition> CODEC = MapCodec.unit(IsNightCondition::new);
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "is_night");

    @Override
    public boolean check(EventContext context) {
        return context.getServer().overworld().isNight();
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
