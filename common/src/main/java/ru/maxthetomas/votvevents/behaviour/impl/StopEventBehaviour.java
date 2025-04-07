package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;

public class StopEventBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "stop_event");
    public static final MapCodec<StopEventBehaviour> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(StopEventBehaviour::new));

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);
        var event = context.getSourceEvent();
        VotvEvents.getEventManager().stopEvent(event);
    }
}