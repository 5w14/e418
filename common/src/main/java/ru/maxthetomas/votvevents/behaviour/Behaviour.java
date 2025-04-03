package ru.maxthetomas.votvevents.behaviour;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.event.ActiveEvent;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * Event behaviour.
 * These behaviours are generic actions that event could perform.
 */
public abstract class Behaviour {
    public abstract ResourceLocation getTypeId();

    private boolean isDisposed = false;
    private boolean isStopped = false;
    private boolean isExecuted = false;
    private boolean isDone = false;

    /**
     * Executes behaviour in event. Used if event with this behaviour starts.
     *
     * @param context Context of event.
     */
    public void execute(EventContext context) {
        isExecuted = true;
    }

    /**
     * Disposes behaviour in event. Used if event with this behaviour ends.
     */
    public void dispose() {
        isDisposed = true;
        isStopped = true;
    }

    /**
     * Stops behaviour. This doesn't actually dispose behaviour, so it could smoothly end its activity.
     */
    public void stop() {
        dispose();
    }

    /**
     * Tries to execute behaviour.
     * This will execute behaviour only if it wasn't executed before.
     */
    public final void tryExecute(EventContext context) {
        if (isExecuted)
            return;
        execute(context);
        isExecuted = true;
    }

    /**
     * Tries to stop behaviour.
     * This will stop behaviour only if it was executed and not stopped before.
     */
    public final void tryStop() {
        if (!isExecuted || isStopped)
            return;
        stop();
        isStopped = true;
    }

    /**
     * Tries to dispose behaviour.
     * This will stop dispose only if it was executed and not disposed before.
     */
    public final void tryDispose() {
        if (!isExecuted || isDisposed)
            return;
        dispose();
        isDisposed = true;
    }

    /**
     * Can this behaviour run.
     * Behaviours are generic. This should return false only if behaviour really can't run.
     * If you want your event don't run in specific conditions (i.e. only at night), use {@link ru.maxthetomas.votvevents.condition.ICondition}
     *
     * @return Is condition can run.
     */
    public boolean canRun(EventContext context) {
        return true;
    }

    /**
     * Changes done state.
     *
     * @param value Done state
     */
    public final void setDone(boolean value) {
        isDone = value;
        // TODO: we need to somehow update behaviours that execute other behaviours too, not just event it was casted from.
        // temporary solution until above is resolved
        for (ActiveEvent activeEvent : VotvEvents.getEventManager().getActiveEvents()) {
            activeEvent.dirty();
        }

    }

    /**
     * Is this behaviour done its activity.
     * Done behaviour means that it's safe to dispose it.
     * If all behaviours on event returns true, the event will be automatically disposed
     *
     * @return Is behaviour done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * @return If behaviour is disposed
     */
    public final boolean isDisposed() {
        return isDisposed;
    }

    /**
     * @return Is behaviour is stopped
     */
    public final boolean isStopped() {
        return isStopped;
    }
}
