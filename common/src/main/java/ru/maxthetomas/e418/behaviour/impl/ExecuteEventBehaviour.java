package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

/**
 * Executes an event.
 * <ul>
 *   <li><code>event</code> – The event to execute.</li>
 * </ul>
 */
public class ExecuteEventBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("execute_event");
    public static final MapCodec<ExecuteEventBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("event").forGetter(ExecuteEventBehaviour::getEventId)
    ).apply(instance, ExecuteEventBehaviour::new));
    public static final MapCodec<ExecuteEventBehaviour> STATE_CODEC = MapCodec.unit(ExecuteEventBehaviour::new);

    private ResourceLocation eventId;

    public ExecuteEventBehaviour(ResourceLocation eventId) {
        this.eventId = eventId;
    }

    private ExecuteEventBehaviour() {
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        var manager = E418.getEventManager();
        var evt = manager.getEvent(getEventId());

        if (evt == null) {
            LogUtils.getLogger().warn("Cannot execute event {} - No event with such ID registered!", eventId);
            return;
        }

        manager.runEvent(evt, context.clone());
    }

    public ResourceLocation getEventId() {
        return eventId;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
