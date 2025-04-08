package ru.maxthetomas.e418.event.cause.impl;

import ru.maxthetomas.e418.event.cause.IEventCause;

/**
 * Cause when event was caused by sleeping.
 */
public class WakeUpEventCause implements IEventCause {
    private boolean isTimeSkipCancelled = false;

    /**
     * @return Is time skip was cancelled
     */
    public boolean isTimeSkipCancelled() {
        return isTimeSkipCancelled;
    }

    /**
     * Cancels time skip after sleeping
     */
    public void cancelTimeSkip() {
        isTimeSkipCancelled = true;
    }
}
