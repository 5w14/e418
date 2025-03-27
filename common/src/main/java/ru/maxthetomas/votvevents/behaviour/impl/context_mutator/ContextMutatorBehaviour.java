package ru.maxthetomas.votvevents.behaviour.impl.context_mutator;

import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.behaviour.PreActiveBehaviour;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Behaviour that mutates context of event for it child behaviours and conditions
 */
public abstract class ContextMutatorBehaviour implements IBehaviour {
    public final List<IBehaviour> activeBehaviours = new ArrayList<>();
    private final List<PreActiveBehaviour> behaviours;
    private final List<ICondition> runConditions;
    public EventContext storedMutatedContext;

    protected ContextMutatorBehaviour(List<PreActiveBehaviour> behaviours, List<ICondition> runConditions) {
        this.behaviours = behaviours;
        this.runConditions = runConditions;
    }

    /**
     * Mutates context to send it to behaviours and conditions.
     *
     * @param context Context to mutate
     * @return Mutated context or null to prevent running.
     */
    public EventContext getMutatedContext(EventContext context) {
        return context;
    }

    @Override
    public boolean canRun(EventContext context) {
        var mutatedContext = getMutatedContext(context);

        if (mutatedContext == null)
            return false;

        if (!mutatedContext.isForced()) {
            // Check if conditions to run are met
            for (ICondition condition : runConditions) {
                if (!condition.check(mutatedContext)) {
                    return false;
                }
            }
        }

        // Check if all behaviours can run
        for (var preActiveBehaviour : behaviours) {
            // todo find a better way to check if behaviour can run
            if (!preActiveBehaviour.create().canRun(mutatedContext)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute(EventContext context) {
        storedMutatedContext = getMutatedContext(context);

        for (var preActiveBehaviour : behaviours) {
            var behaviour = preActiveBehaviour.create();
            behaviour.execute(storedMutatedContext);
            activeBehaviours.add(behaviour);
        }
    }

    @Override
    public void dispose() {
        for (IBehaviour behaviour : activeBehaviours) {
            behaviour.dispose();
        }
    }

    @Override
    public boolean isDone() {
        for (IBehaviour behaviour : this.activeBehaviours) {
            if (!behaviour.isDone())
                return false;
        }
        return true;
    }

    public List<PreActiveBehaviour> getBehaviours() {
        return behaviours;
    }

    public List<ICondition> getRunConditions() {
        return runConditions;
    }
}
