package ru.maxthetomas.votvevents.behaviour;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * Event behaviour.
 * These behaviours are generic actions that event could perform.
 */
public interface IBehaviour {
    public ResourceLocation getTypeId();

    /**
     * Executes behaviour in event. Used if event with this behaviour starts.
     *
     * @param context Context of event.
     */
    void execute(EventContext context);

    /**
     * Disposes behaviour in event. Used if event with this behaviour ends.
     */
    default void dispose() {
    }

    /**
     * Stops behaviour. This doesn't actually dispose behaviour, so it could smoothly end it's activity.
     */
    default void stop() {
        dispose();
    }

    /**
     * Can this behaviour run.
     * Behaviours are generic. This should return false only if behaviour really can't run.
     * If you want your event don't run in specific conditions (i.e. only at night), use {@link ru.maxthetomas.votvevents.condition.ICondition}
     *
     * @return Is condition can run.
     */
    default boolean canRun(EventContext context) {
        return true;
    }

    /**
     * Is this behaviour done it's part. If all behaviours on event returns true, the event will be automatically disposed.
     * Instant behaviours better to return true, while continuous only false to rely on ending behaviours.
     *
     * @return Is behaviour done.
     */
    default boolean isDone() {
        return true;
    }
}
