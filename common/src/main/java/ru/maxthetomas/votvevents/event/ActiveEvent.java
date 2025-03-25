package ru.maxthetomas.votvevents.event;

import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;

/**
 * Event that is currently active.
 */
public class ActiveEvent {
    public final EventResource resource;
    public final EventContext context;
    public final long startTime;

    public ActiveEvent(EventResource resource, EventContext context, long startTime) {
        this.resource = resource;
        this.context = context;
        this.startTime = startTime;
    }

    public void updateState() {
        for (IBehaviour behaviour : resource.behaviourList) {
            if (!behaviour.isDone(context)) {
                return;
            }
        }
        VotvEvents.getEventManager().endEvent(this);
    }
}
