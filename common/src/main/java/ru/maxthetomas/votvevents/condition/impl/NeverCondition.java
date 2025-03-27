package ru.maxthetomas.votvevents.condition.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * A condition that always fails.
 */
public class NeverCondition implements ICondition {
    public static final MapCodec<NeverCondition> CODEC = MapCodec.unit(NeverCondition::new);
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "never");

    @Override
    public boolean check(EventContext context) {
        return false;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
