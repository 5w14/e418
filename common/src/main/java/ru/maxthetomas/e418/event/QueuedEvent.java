package ru.maxthetomas.e418.event;

import org.jetbrains.annotations.Nullable;

public record QueuedEvent(EventResource resource, EventContext context, @Nullable Long timeoutTick) {
    @Override
    public String toString() {
        if (timeoutTick != null)
            return String.format("QueuedEvent[%s] (times out at [%s])", resource.name(), timeoutTick);

        return String.format("QueuedEvent[%s] (no timeout)", resource.name());
    }
}
