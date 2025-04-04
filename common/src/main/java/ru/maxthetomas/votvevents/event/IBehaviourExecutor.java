package ru.maxthetomas.votvevents.event;

import ru.maxthetomas.votvevents.behaviour.Behaviour;

import java.util.List;

public interface IBehaviourExecutor {

    List<Behaviour> getBehaviours();

    default boolean isDone() {
        for (Behaviour behaviour : this.getBehaviours()) {
            if (!behaviour.isDone())
                return false;
        }
        return true;
    }

    default void dirty() {
    }

    default boolean isDirty() {
        return false;
    }

    default void undirty() {

    }
}
