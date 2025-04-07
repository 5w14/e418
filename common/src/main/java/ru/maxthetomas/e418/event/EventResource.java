package ru.maxthetomas.e418.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ru.maxthetomas.e418.behaviour.PreActiveBehaviour;
import ru.maxthetomas.e418.condition.Conditions;
import ru.maxthetomas.e418.condition.ICondition;

import java.util.List;

/**
 * Event resource.
 */
public record EventResource(String name, String description, List<PreActiveBehaviour> behaviourList,
                            List<ICondition> runConditions, List<ICondition> queueConditions) {

    public static final MapCodec<EventResource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(EventResource::name),
            Codec.STRING.optionalFieldOf("description", "").forGetter(EventResource::description),
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter(EventResource::behaviourList),
            Conditions.DISPATCH_CODEC.listOf().fieldOf("run_conditions").forGetter(EventResource::runConditions),
            Conditions.DISPATCH_CODEC.listOf().fieldOf("queue_conditions").forGetter(EventResource::queueConditions)
    ).apply(instance, EventResource::new));

    /**
     * Checks if this event can run.
     *
     * @return Is event can run at this moment
     */
    public boolean canRun(EventContext context) {
        if (!context.isForced()) {
            // Check if conditions to run are met
            for (ICondition condition : runConditions) {
                if (!condition.check(context)) {
                    return false;
                }
            }
        }

        // Check if all behaviours can run
        for (PreActiveBehaviour preActiveBehaviour : behaviourList) {
            // todo find a better way to check if behaviour can run
            if (!preActiveBehaviour.create().canRun(context)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if this event can be queued.
     * If event can't run, it will usually be queued to wait when it can run.
     *
     * @return Is event can be queued at this moment
     */
    public boolean canQueue(EventContext context) {
        // Check if conditions to queue are met
        for (ICondition condition : queueConditions) {
            if (!condition.check(context)) {
                return false;
            }
        }

        return true;
    }
}
