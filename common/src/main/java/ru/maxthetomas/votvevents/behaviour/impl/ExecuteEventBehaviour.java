package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;

public class ExecuteEventBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "execute_event");
    public static final MapCodec<ExecuteEventBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("event").forGetter(ExecuteEventBehaviour::getEventId)
    ).apply(instance, ExecuteEventBehaviour::new));

    private final ResourceLocation eventId;

    public ExecuteEventBehaviour(ResourceLocation eventId) {
        this.eventId = eventId;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);

        var manager = VotvEvents.getEventManager();
        var evt = manager.getEvent(getEventId());

        if (evt == null) {
            LogUtils.getLogger().warn("Cannot execute event {} - No event with such event registered!", eventId);
            return;
        }

        manager.runEvent(evt, context);
    }

    public ResourceLocation getEventId() {
        return eventId;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
