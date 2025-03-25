package ru.maxthetomas.votvevents.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.condition.ICondition;

import java.util.List;

/**
 * Event resource.
 */
public class EventResource {
    List<IBehaviour> behaviourList;
    List<ICondition> runConditions;
    List<ICondition> queueConditions;

    // TODO: cool minecraft formatted strings
    String name;
    String description;

    /**
     * Constructs event
     * @param name Name of event
     * @param description Description of event
     * @param behaviourList Behaviour of event
     * @param runConditions Run conditions of event
     * @param queueConditions Queue conditions of event
     */
    public EventResource(String name, String description, List<IBehaviour> behaviourList, List<ICondition> runConditions, List<ICondition> queueConditions)
    {
        this.name = name;
        this.description = description;
        this.behaviourList = behaviourList;
        this.runConditions = runConditions;
        this.queueConditions = queueConditions;
    }

    /**
     * Constructs event from JSON
     * @param json JSON to use for construction
     */
    public EventResource(JsonObject json)
    {
        this.name = json.get("name").getAsString();
        this.description = json.get("description").getAsString();

        // Setup behaviours from JSON
        var jsonBehaviours = json.get("behaviours").getAsJsonArray();
        for (JsonElement jsonElement : jsonBehaviours)
        {
            var jsonBehaviour = jsonElement.getAsJsonObject();
            var id = jsonBehaviour.get("id").getAsString();
            var nullableProperties = jsonBehaviour.get("properties");

            // TODO: actually add behaviours
            if (nullableProperties != null){
                var properties = nullableProperties.getAsJsonObject();
                // initialization with properties
            }
            else {
                // empty initialization
            }
        }

        // Setup run conditions from JSON
        var jsonRunConditions = json.get("run_conditions").getAsJsonArray();
        for (JsonElement jsonElement : jsonRunConditions)
        {
            var jsonBehaviour = jsonElement.getAsJsonObject();
            var id = jsonBehaviour.get("id").getAsString();
            var nullableProperties = jsonBehaviour.get("properties");

            // TODO: actually add behaviours
            if (nullableProperties != null){
                var properties = nullableProperties.getAsJsonObject();
                // initialization with properties
            }
            else {
                // empty initialization
            }
        }

        // Setup queue conditions from JSON
        var jsonQueueConditions = json.get("run_conditions").getAsJsonArray();
        for (JsonElement jsonElement : jsonQueueConditions)
        {
            var jsonBehaviour = jsonElement.getAsJsonObject();
            var id = jsonBehaviour.get("id").getAsString();
            var nullableProperties = jsonBehaviour.get("properties");

            // TODO: actually add behaviours
            if (nullableProperties != null){
                var properties = nullableProperties.getAsJsonObject();
                // initialization with properties
            }
            else {
                // empty initialization
            }
        }
    }

    /**
     * Checks if this event can run.
     * @return Is event can run at this moment
     */
    public boolean canRun()
    {
        // Check if conditions to run are met
        for (ICondition condition : runConditions){
            if (!condition.check())
            {
                return false;
            }
        }

        // Check if all behaviours can run
        for (IBehaviour behaviour : behaviourList){
            if (!behaviour.canRun())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if this event can be queued.
     * If event can't run, it will usually be queued to wait when it can run.
     * @return Is event can be queued at this moment
     */
    public boolean canQueue()
    {
        // Check if conditions to queue are met
        for (ICondition condition : queueConditions){
            if (!condition.check())
            {
                return false;
            }
        }

        return true;
    }


}
