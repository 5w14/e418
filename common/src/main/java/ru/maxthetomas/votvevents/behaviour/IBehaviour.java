package ru.maxthetomas.votvevents.behaviour;

/**
 * Event behaviour.
 * These behaviours are generic actions that event could perform.
 */
public interface IBehaviour {

    /**
     * Can this behaviour run.
     * Behaviours are generic. This should return false only if behaviour really can't run.
     * If you want your event don't run in specific conditions (i.e. only at night), use {@link ru.maxthetomas.votvevents.condition.ICondition}
     * @return Is condition can run.
     */
    default boolean canRun(){
        return true;
    }
}
