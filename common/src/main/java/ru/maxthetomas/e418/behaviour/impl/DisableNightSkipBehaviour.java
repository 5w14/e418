package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.util.E418Variables;

/**
 * Makes so that you cannot sleep
 */
public class DisableNightSkipBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "disable_night_skip");
    public static final MapCodec<DisableNightSkipBehaviour> CODEC = MapCodec.unit(new DisableNightSkipBehaviour());

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        E418Variables.DisableNightSkip = true;
    }

    @Override
    public void dispose() {
        super.dispose();
        E418Variables.DisableNightSkip = false;
    }
}
