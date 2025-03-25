package ru.maxthetomas.votvevents.condition;

/**
 * Event condition to run or queue.
 * These conditions are generic actions that event could check.
 */
public interface ICondition {

    /**
     * Check if condition is met
     * @return Is condition is met
     */
    default boolean check(){
        return true;
    }
}
