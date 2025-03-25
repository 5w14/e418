package ru.maxthetomas.votvevents.event;

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
}
