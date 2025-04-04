package ru.maxthetomas.votvevents.event;

import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;

import java.util.ArrayList;
import java.util.List;

/**
 * Event that is currently active.
 */
public class ActiveEvent implements IBehaviourExecutor {
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
            VotvEvents.getEventManager().disposeEvent(this);
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
    public List<Behaviour> getBehaviours() {
        return activeBehaviours;
    }
}
