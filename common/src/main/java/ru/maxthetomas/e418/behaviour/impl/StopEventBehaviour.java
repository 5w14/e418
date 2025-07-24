package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

/**
 * Stops the event.
 */
public class StopEventBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "stop_event");
    public static final MapCodec<StopEventBehaviour> CODEC = MapCodec.unit(StopEventBehaviour::new);
    public static final MapCodec<StopEventBehaviour> STATE_CODEC = MapCodec.unit(StopEventBehaviour::new);

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);
        var event = context.getSourceEvent();
        E418.getEventManager().stopEvent(event);
    }

    @Override
    public void restoreState(EventContext context, IBehaviourExecutor executor) {
        super.restoreState(context, executor);
        if ((isDisposed() || isDone() || isExecuted()) && context.getSourceEvent() != null)
            E418.getEventManager().stopEvent(context.getSourceEvent());
    }
}