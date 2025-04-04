package ru.maxthetomas.votvevents.event;

import ru.maxthetomas.votvevents.behaviour.Behaviour;

import java.util.List;

public interface IBehaviourExecutor {

    /**
     * @return List of executed behaviours
     */
    List<Behaviour> getExecutedBehaviours();

    /**
     * @return Are all behaviours in this executor are done
     */
    default boolean isDone() {
        for (Behaviour behaviour : this.getExecutedBehaviours()) {
            if (!behaviour.isDone())
                return false;
        }
        return true;
    }

    /**
     * Marks executor as dirty. Dirty executor will check if all behaviours are done and react if they are.
     * After this check, executor will undirty.
     */
    default void dirty() {
    }

    /**
     * @return Is dirty
     */
    default boolean isDirty() {
        return false;
    }

    /**
     * Unmarks executor as dirty.
     */
    default void undirty() {

    }
}
