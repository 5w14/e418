package ru.maxthetomas.votvevents.event;

import org.jetbrains.annotations.Nullable;

public final class QueuedEvent {
    public final EventResource resource;
    public final EventContext context;

    @Nullable
    public final Long timeoutTick;

    public QueuedEvent(EventResource resource, EventContext context, @Nullable Long timeoutTick) {
        this.resource = resource;
        this.context = context;
        this.timeoutTick = timeoutTick;
    }

    @Override
    public String toString() {
        if (timeoutTick != null)
            return String.format("QueuedEvent[%s] (times out at [%s])", resource.name(), timeoutTick);
        
        return String.format("QueuedEvent[%s] (no timeout)", resource.name());
    }
}
