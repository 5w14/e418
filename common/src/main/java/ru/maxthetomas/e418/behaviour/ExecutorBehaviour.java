package ru.maxthetomas.e418.behaviour;

import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.List;

public abstract class ExecutorBehaviour extends Behaviour implements IBehaviourExecutor {
    protected final List<PreActiveBehaviour> behaviours;
    protected EventContext context;
    protected List<Behaviour> activeBehaviours = List.of();

    public ExecutorBehaviour(List<PreActiveBehaviour> behaviours) {
        this.behaviours = behaviours;
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
        if (activeBehaviours.isEmpty())
            activeBehaviours = behaviours.stream().map(PreActiveBehaviour::create).toList();

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
    public List<Behaviour> getExecutedBehaviours() {
        return activeBehaviours;
    }

    public List<PreActiveBehaviour> getPreActiveBehaviours() {
        return behaviours;
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
