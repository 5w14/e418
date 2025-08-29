package ru.maxthetomas.e418.behaviour;

import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.List;

public abstract class ExecutorBehaviour extends Behaviour implements IBehaviourExecutor {
    protected List<Behaviour> activeBehaviours;

    public ExecutorBehaviour(List<PreActiveBehaviour> behaviours) {
        this.activeBehaviours = behaviours.stream().map(PreActiveBehaviour::create).toList();
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        this.context = context;
        super.execute(context, executor);
    }

    /**
     * Attempts to create and start pre-active behaviours.
     *
     * @return true if start is successful.
     */
    protected boolean tryStartBehaviours() {
        if (activeBehaviours.stream().anyMatch(v -> !v.canRun(context)))
            return false;

        activeBehaviours.forEach(b -> b.tryExecute(context, this));

        return true;
    }

    @Override
    public void tick() {
        super.tick();
        activeBehaviours.forEach(Behaviour::tick);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Behaviour behaviour : activeBehaviours) {
            behaviour.dispose();
        }
    }

    @Override
    public void stop() {
        super.stop();
        for (Behaviour behaviour : activeBehaviours) {
            behaviour.stop();
        }
    }

    @Override
    public void restoreState(EventContext context, IBehaviourExecutor executor) {
        super.restoreState(context, executor);
        activeBehaviours.forEach(v -> v.restoreState(context, this));
        dirty();
    }

    @Override
    public List<Behaviour> getExecutedBehaviours() {
        return activeBehaviours;
    }

    @Override
    public void dirty() {
        for (Behaviour behaviour : this.activeBehaviours) {
            if (!behaviour.isDone()) {
                setDone(false);
                return;
            }
        }
        setDone(true);
    }
}
