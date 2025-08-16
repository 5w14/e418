package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.ActiveBehaviourDispatch;
import ru.maxthetomas.e418.behaviour.ExecutorBehaviour;
import ru.maxthetomas.e418.behaviour.PreActiveBehaviour;
import ru.maxthetomas.e418.condition.Conditions;
import ru.maxthetomas.e418.condition.ICondition;

import java.util.List;
import java.util.function.Function;

/**
 * Waits until conditions are true and executes behaviours.
 * <ul>
 *   <li><code>conditions</code> - Conditions to run.</li>
 *   <li><code>behaviours</code> - Behaviours to execute.</li>
 * </ul>
 */
public class WaitForConditionBehaviour extends ExecutorBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "wait_for_conditions");
    public static final MapCodec<WaitForConditionBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Conditions.DISPATCH_CODEC.listOf().fieldOf("conditions").forGetter(WaitForConditionBehaviour::getConditions),
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter((a) -> List.of())
    ).apply(instance, WaitForConditionBehaviour::new));

    public static final MapCodec<WaitForConditionBehaviour> STATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CODEC.fieldOf("data").forGetter(Function.identity()),
            Codec.BOOL.lenientOptionalFieldOf("started", false).forGetter(v -> v.started),
            ActiveBehaviourDispatch.DISPATCH_CODEC.listOf().fieldOf("active_behaviours").forGetter(v -> v.activeBehaviours)
    ).apply(instance, (self, started, activeBehaviours) -> {
        self.started = started;
        self.activeBehaviours = activeBehaviours;
        return self;
    }));

    private final List<ICondition> conditions;
    private boolean started = false;

    public WaitForConditionBehaviour(List<ICondition> conditions, List<PreActiveBehaviour> behaviours) {
        super(behaviours);
        this.conditions = conditions;
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
