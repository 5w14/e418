package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.behaviour.PreActiveBehaviour;
import ru.maxthetomas.votvevents.behaviour.contextmutators.ContextMutators;
import ru.maxthetomas.votvevents.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.votvevents.condition.Conditions;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

import java.util.ArrayList;
import java.util.List;

public class MutateContextBehaviour implements IBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "mutate_context");
    public static final MapCodec<MutateContextBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ContextMutators.DISPATCH_CODEC.listOf().fieldOf("context_mutators").forGetter(MutateContextBehaviour::getContextMutators),
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter(MutateContextBehaviour::getBehaviours),
            Conditions.DISPATCH_CODEC.listOf().fieldOf("run_conditions").forGetter(MutateContextBehaviour::getRunConditions)
    ).apply(instance, MutateContextBehaviour::new));

    public final List<IContextMutator> contextMutators;
    public final List<PreActiveBehaviour> behaviours;
    public final List<ICondition> runConditions;

    public final List<IBehaviour> activeBehaviours = new ArrayList<>();
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

    public List<PreActiveBehaviour> getBehaviours() {
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
}
