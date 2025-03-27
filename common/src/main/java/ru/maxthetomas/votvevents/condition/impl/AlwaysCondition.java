package ru.maxthetomas.votvevents.condition.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

public class AlwaysCondition implements ICondition {
    public static final MapCodec<AlwaysCondition> CODEC = MapCodec.unit(AlwaysCondition::new);
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "never");

    @Override
    public boolean check(EventContext context) {
        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
