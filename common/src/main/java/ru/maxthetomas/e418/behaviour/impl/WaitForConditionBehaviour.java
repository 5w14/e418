package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.ExecutorBehaviour;
import ru.maxthetomas.e418.behaviour.PreActiveBehaviour;
import ru.maxthetomas.e418.condition.Conditions;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.List;

public class WaitForConditionBehaviour extends ExecutorBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "wait_for_conditions");
    public static final MapCodec<WaitForConditionBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Conditions.DISPATCH_CODEC.listOf().fieldOf("conditions").forGetter(WaitForConditionBehaviour::getConditions),
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter(WaitForConditionBehaviour::getPreActiveBehaviours)
    ).apply(instance, WaitForConditionBehaviour::new));

    private final List<ICondition> conditions;
    private EventContext context;

    private boolean started = false;

    public WaitForConditionBehaviour(List<ICondition> conditions, List<PreActiveBehaviour> behaviours) {
        super(behaviours);
        this.conditions = conditions;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        this.context = context;
    }

    @Override
    public void tick() {
        super.tick();

        if (isDone() || started) return;

        var allChecksSucceeded = conditions.stream().allMatch(c -> c.check(context));
        if (!allChecksSucceeded) return;

        started = tryStartBehaviours();
    }

    public List<ICondition> getConditions() {
        return conditions;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
