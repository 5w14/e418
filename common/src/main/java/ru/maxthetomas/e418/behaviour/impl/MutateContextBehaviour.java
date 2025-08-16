package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.ActiveBehaviourDispatch;
import ru.maxthetomas.e418.behaviour.ExecutorBehaviour;
import ru.maxthetomas.e418.behaviour.PreActiveBehaviour;
import ru.maxthetomas.e418.behaviour.contextmutators.ContextMutators;
import ru.maxthetomas.e418.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.e418.condition.Conditions;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.List;
import java.util.function.Function;

/**
 * Changes the event's context.
 * <ul>
 *   <li><code>mutators</code> – Mutators that it will use.</li>
 *   <li><code>behaviours</code> – Behaviours that will use the new context.</li>
 *   <li><code>run_conditions</code> – When this should run.</li>
 * </ul>
 */
public class MutateContextBehaviour extends ExecutorBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "mutate_context");
    public static final MapCodec<MutateContextBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ContextMutators.DISPATCH_CODEC.listOf().fieldOf("mutators").forGetter(MutateContextBehaviour::getContextMutators),
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter((a) -> List.of()),
            Conditions.DISPATCH_CODEC.listOf().optionalFieldOf("run_conditions", List.of()).forGetter(MutateContextBehaviour::getRunConditions)
    ).apply(instance, MutateContextBehaviour::new));

    public static final MapCodec<MutateContextBehaviour> STATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CODEC.fieldOf("data").forGetter(Function.identity()),
            EventContext.CODEC.fieldOf("stored_context").forGetter(v -> v.storedMutatedContext),
            ActiveBehaviourDispatch.DISPATCH_CODEC.listOf().fieldOf("active_behaviours").forGetter(v -> v.activeBehaviours)
    ).apply(instance, (self, context, children) -> {
        self.storedMutatedContext = context;
        self.activeBehaviours = children;
        return self;
    }));


    public final List<IContextMutator> contextMutators;
    public final List<ICondition> runConditions;

    public EventContext storedMutatedContext;

    public MutateContextBehaviour(List<IContextMutator> contextMutators, List<PreActiveBehaviour> behaviours, List<ICondition> runConditions) {
        super(behaviours);
        this.contextMutators = contextMutators;
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

        this.context = storedMutatedContext;

        tryStartBehaviours();
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
        for (var behaviour : activeBehaviours) {
            if (!behaviour.canRun(mutatedContext)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void restoreState(EventContext context, IBehaviourExecutor executor) {
        // Does not have a super.restoreState() call as it modifies the restoreCall for active behaviours
        this.executor = executor;
        this.context = context;
        activeBehaviours.forEach(v -> v.restoreState(this.storedMutatedContext, this));
        dirty();
    }
}
