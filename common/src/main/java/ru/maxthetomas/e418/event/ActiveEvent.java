package ru.maxthetomas.e418.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.ActiveBehaviourDispatch;
import ru.maxthetomas.e418.behaviour.Behaviour;

import java.util.ArrayList;
import java.util.List;

/**
 * Event that is currently active.
 */
public class ActiveEvent implements IBehaviourExecutor {
    public static final MapCodec<ActiveEvent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.xmap(v -> E418.getEventManager().getEvent(v),
                            res -> E418.getEventManager().getResourceLocation(res)).fieldOf("id")
                    .forGetter(v -> v.resource),
            EventContext.CODEC.fieldOf("context").forGetter(v -> v.context),
            Codec.LONG.fieldOf("start_time").forGetter(v -> v.startTime),
            ActiveBehaviourDispatch.CODEC.codec().listOf().fieldOf("active_behaviours")
                    .forGetter(v -> v.activeBehaviours.stream().map(ActiveBehaviourDispatch::create).toList())
    ).apply(i, ActiveEvent::createFromCodec));

    public final EventResource resource;
    public final EventContext context;
    public final long startTime;
    public final List<Behaviour> activeBehaviours = new ArrayList<>();
    private boolean dirty = false;

    public ActiveEvent(EventResource resource, EventContext context, long startTime) {
        this.resource = resource;
        this.context = context;
        this.startTime = startTime;
    }

    public void updateState() {
        if (isDone())
            E418.getEventManager().disposeEvent(this);
    }


    public void disposeBehaviours() {
        for (Behaviour activeBehaviour : activeBehaviours) {
            activeBehaviour.tryDispose();
        }
    }

    public void stopBehaviours() {
        for (Behaviour activeBehaviour : activeBehaviours) {
            activeBehaviour.tryStop();
        }
    }

    public void tick() {
        for (Behaviour activeBehaviour : activeBehaviours) {
            activeBehaviour.tick();
        }
    }

    @Override
    public String toString() {
        return String.format("ActiveEvent[%s] (started at: %s, active behaviours: %s)", resource.name(), startTime, activeBehaviours.size());
    }

    public boolean isDone() {
        for (Behaviour behaviour : this.activeBehaviours) {
            if (!behaviour.isDone())
                return false;
        }
        return true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void dirty() {
        this.dirty = true;
    }

    @Override
    public void undirty() {
        this.dirty = false;
    }

    @Override
    public List<Behaviour> getExecutedBehaviours() {
        return activeBehaviours;
    }


    /**
     * Restores active state for events during reloading from a save.
     */
    public void _restoreState() {
        activeBehaviours.forEach(behaviour -> {
            if (behaviour.isDisposed()) {
                return;
            }

            behaviour.restoreState(context, this);
        });
    }

    private static ActiveEvent createFromCodec(EventResource resource, EventContext context,
                                               long startTime, List<ActiveBehaviourDispatch<Behaviour>> activeBehaviours) {
        var event = new ActiveEvent(resource, context, startTime);
        event.activeBehaviours.addAll(activeBehaviours.stream()
                .map(ActiveBehaviourDispatch::getActiveBehaviour).toList());
        event.dirty();
        return event;
    }
}
