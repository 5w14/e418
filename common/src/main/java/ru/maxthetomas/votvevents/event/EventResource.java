package ru.maxthetomas.votvevents.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.behaviour.Behaviours;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.condition.Conditions;
import ru.maxthetomas.votvevents.condition.ICondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Event resource.
 */
public class EventResource {
    List<PreActiveBehaviour> behaviourList;
    List<ICondition> runConditions;
    List<ICondition> queueConditions;

    String name;
    String description;

    /**
     * Constructs event
     *
     * @param name            Name of event
     * @param description     Description of event
     * @param behaviourList   Behaviour of event
     * @param runConditions   Run conditions of event
     * @param queueConditions Queue conditions of event
     */
    public EventResource(String name, String description, List<PreActiveBehaviour> behaviourList, List<ICondition> runConditions, List<ICondition> queueConditions) {
        this.name = name;
        this.description = description;
        this.behaviourList = behaviourList;
        this.runConditions = runConditions;
        this.queueConditions = queueConditions;
    }

    /**
     * Constructs event from JSON
     *
     * @param json JSON to use for construction
     */
    public static EventResource buildEventResourceFromJson(JsonObject json) {
        try {
            var name = json.get("name").getAsString();
            var description = json.get("description").getAsString();

            // Setup behaviours from JSON
            List<PreActiveBehaviour> behaviours = new ArrayList<>();
            var jsonBehaviours = json.get("behaviours").getAsJsonArray();
            for (JsonElement jsonElement : jsonBehaviours) {
                var jsonBehaviour = jsonElement.getAsJsonObject();
                var id = jsonBehaviour.get("id").getAsString();
                var nullableProperties = jsonBehaviour.get("properties");
                var behaviour = Behaviours.getBehaviourBuilder(ResourceLocation.tryParse(id));
                behaviours.add(new PreActiveBehaviour(behaviour, nullableProperties));
            }

            // Setup run conditions from JSON
            List<ICondition> runConditions = new ArrayList<>();
            var jsonRunConditions = json.get("run_conditions").getAsJsonArray();
            for (JsonElement jsonElement : jsonRunConditions) {
                var jsonBehaviour = jsonElement.getAsJsonObject();
                var id = jsonBehaviour.get("id").getAsString();
                var nullableProperties = jsonBehaviour.get("properties");
                var condition = Conditions.createCondition(ResourceLocation.tryParse(id), nullableProperties);
                runConditions.add(condition);
            }

            // Setup queue conditions from JSON
            List<ICondition> queueConditions = new ArrayList<>();
            var jsonQueueConditions = json.get("run_conditions").getAsJsonArray();
            for (JsonElement jsonElement : jsonQueueConditions) {
                var jsonBehaviour = jsonElement.getAsJsonObject();
                var id = jsonBehaviour.get("id").getAsString();
                var nullableProperties = jsonBehaviour.get("properties");
                var condition = Conditions.createCondition(ResourceLocation.tryParse(id), nullableProperties);
                runConditions.add(condition);
            }
            return new EventResource(name, description, behaviours, runConditions, queueConditions);
        } catch (IllegalStateException | UnsupportedOperationException | NullPointerException e) {
            return null;
        }
    }

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public record PreActiveBehaviour(Behaviours.Builder behaviour, JsonElement properties) {
        public IBehaviour create() {
            return behaviour.apply(properties);
        }
    }
}
