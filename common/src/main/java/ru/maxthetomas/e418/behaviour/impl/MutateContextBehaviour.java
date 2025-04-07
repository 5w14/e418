package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.behaviour.PreActiveBehaviour;
import ru.maxthetomas.e418.behaviour.contextmutators.ContextMutators;
import ru.maxthetomas.e418.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.e418.condition.Conditions;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.ArrayList;
import java.util.List;

public class MutateContextBehaviour extends Behaviour implements IBehaviourExecutor {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "mutate_context");
    public static final MapCodec<MutateContextBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ContextMutators.DISPATCH_CODEC.listOf().fieldOf("mutators").forGetter(MutateContextBehaviour::getContextMutators),
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter(MutateContextBehaviour::getPreActiveBehaviours),
            Conditions.DISPATCH_CODEC.listOf().optionalFieldOf("run_conditions", List.of()).forGetter(MutateContextBehaviour::getRunConditions)
    ).apply(instance, MutateContextBehaviour::new));

    public final List<IContextMutator> contextMutators;
    public final List<PreActiveBehaviour> behaviours;
    public final List<ICondition> runConditions;

    public final List<Behaviour> activeBehaviours = new ArrayList<>();
    public EventContext storedMutatedContext;

    public MutateContextBehaviour(List<IContextMutator> contextMutators, List<PreActiveBehaviour> behaviours, List<ICondition> runConditions) {
        this.contextMutators = contextMutators;
        this.behaviours = behaviours;
        this.runConditions = runConditions;
    }

    /**
     * Mutates context to send it to behaviours and conditions.
     *
     * @param context Context to mutate
     * @return New mutated context or null if mutation was failed.
     */
    @Nullable
    public EventContext getMutatedContext(EventContext context) {
        var mutatedContext = context.clone();

        for (IContextMutator contextMutator : contextMutators) {
            if (!contextMutator.mutate(mutatedContext)) {
                return null;
            }
        }

        return mutatedContext;
    }

    public List<ICondition> getRunConditions() {
        return runConditions;
    }

    public List<PreActiveBehaviour> getPreActiveBehaviours() {
        return behaviours;
    }

    public List<IContextMutator> getContextMutators() {
        return contextMutators;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        storedMutatedContext = getMutatedContext(context);

        if (storedMutatedContext == null)
            return;

        for (var preActiveBehaviour : behaviours) {
            var behaviour = preActiveBehaviour.create();
            behaviour.tryExecute(storedMutatedContext, this);
            activeBehaviours.add(behaviour);
        }
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
}
