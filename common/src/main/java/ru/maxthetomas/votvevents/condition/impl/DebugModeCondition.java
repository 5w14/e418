package ru.maxthetomas.votvevents.condition.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * A condition that succeeds only if it is debug mode.
 *
 * @see ru.maxthetomas.votvevents.config.Config
 */
public class DebugModeCondition implements ICondition {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "debug_mode");
    public static MapCodec<DebugModeCondition> CODEC = MapCodec.unit(DebugModeCondition::new);

    @Override
    public boolean check(EventContext context) {
        return VotvEvents.getConfig().orElseThrow().isDebug();
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
