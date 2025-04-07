package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;
import ru.maxthetomas.votvevents.util.VotvEventsVariables;

public class DisableNightSkipBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "disable_night_skip");
    public static final MapCodec<DisableNightSkipBehaviour> CODEC = MapCodec.unit(new DisableNightSkipBehaviour());

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        VotvEventsVariables.DisableNightSkip = true;
    }

    @Override
    public void stop() {
        super.stop();
        setDone(true);
        VotvEventsVariables.DisableNightSkip = false;
    }
}
