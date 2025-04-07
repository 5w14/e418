package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.behaviour.PreActiveBehaviour;
import ru.maxthetomas.e418.condition.Conditions;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.List;

public class WaitForConditionBehaviour extends Behaviour implements IBehaviourExecutor {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "wait_for_conditions");
    public static final MapCodec<WaitForConditionBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Conditions.DISPATCH_CODEC.listOf().fieldOf("conditions").forGetter(WaitForConditionBehaviour::getConditions),
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter(WaitForConditionBehaviour::getBehaviours)
    ).apply(instance, WaitForConditionBehaviour::new));

    private final List<ICondition> conditions;
    private final List<PreActiveBehaviour> behaviours;
    private EventContext context;

    private boolean started = false;
    private List<Behaviour> executedBehaviours = List.of();

    public WaitForConditionBehaviour(List<ICondition> conditions, List<PreActiveBehaviour> behaviours) {
        this.conditions = conditions;
        this.behaviours = behaviours;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);

        this.context = context;

        TickEvent.SERVER_PRE.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        if (isDone() || started) return;

        var allChecksSucceeded = conditions.stream().allMatch(c -> c.check(context));
        if (!allChecksSucceeded) return;

        if (executedBehaviours.isEmpty())
            executedBehaviours = behaviours.stream().map(PreActiveBehaviour::create).toList();

        // If any are unable to run - stop.
        if (executedBehaviours.stream().noneMatch(b -> b.canRun(context)))
            return;

        started = true;

        for (Behaviour behaviour : executedBehaviours) {
            behaviour.tryExecute(context, this);
        }
    }

    public List<ICondition> getConditions() {
        return conditions;
    }

    public List<PreActiveBehaviour> getBehaviours() {
        return behaviours;
    }


    @Override
    public List<Behaviour> getExecutedBehaviours() {
        return executedBehaviours;
    }

    @Override
    public void dirty() {
        for (Behaviour behaviour : this.executedBehaviours) {
            if (!behaviour.isDone()) {
                setDone(false);
                return;
            }
        }
        setDone(started);
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void stop() {
        super.stop();
        for (Behaviour behaviour : executedBehaviours) {
            behaviour.stop();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        TickEvent.SERVER_PRE.unregister(this::tick);
        for (Behaviour behaviour : executedBehaviours) {
            behaviour.dispose();
        }
    }
}
