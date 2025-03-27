package ru.maxthetomas.votvevents.behaviour.impl.context_mutator;

//import com.google.gson.JsonElement;
//import net.minecraft.resources.ResourceLocation;
//import ru.maxthetomas.votvevents.behaviour.Behaviours;
//import ru.maxthetomas.votvevents.behaviour.IBehaviour;
//import ru.maxthetomas.votvevents.condition.Conditions;
//import ru.maxthetomas.votvevents.condition.ICondition;
//import ru.maxthetomas.votvevents.event.EventContext;
//import ru.maxthetomas.votvevents.event.EventResource;
//
//import java.util.ArrayList;
//import java.util.List;

/**
 * Behaviour that mutates context of event for it child behaviours and conditions
 */
//public abstract class ContextMutatorBehaviour implements IBehaviour {
//    List<EventResource.PreActiveBehaviour> behaviours = new ArrayList<>();
//    List<ICondition> runConditions = new ArrayList<>();
//    public final List<IBehaviour> activeBehaviours = new ArrayList<>();
//    public EventContext storedMutatedContext;
//
//    /**
//     * Mutates context to send it to behaviours and conditions.
//     *
//     * @param context Context to mutate
//     * @return Mutated context or null to prevent running.
//     */
//    public EventContext getMutatedContext(EventContext context) {
//        return context;
//    }
//
//    public ContextMutatorBehaviour(JsonElement properties) {
//        var jsonObject = properties.getAsJsonObject();
//
//        // This must be present
//        var jsonBehaviours = jsonObject.get("behaviours").getAsJsonArray();
//        for (JsonElement jsonElement : jsonBehaviours) {
//            var jsonBehaviour = jsonElement.getAsJsonObject();
//            var id = jsonBehaviour.get("id").getAsString();
//            var nullableProperties = jsonBehaviour.get("properties");
//            var behaviour = Behaviours.getBehaviourBuilder(ResourceLocation.tryParse(id));
//            behaviours.add(new EventResource.PreActiveBehaviour(behaviour, nullableProperties));
//        }
//
//        // This could be not present
//        if (jsonObject.has("run_conditions")) {
//            for (JsonElement jsonElement : jsonObject.get("run_conditions").getAsJsonArray()) {
//                var jsonBehaviour = jsonElement.getAsJsonObject();
//                var id = jsonBehaviour.get("id").getAsString();
//                var nullableProperties = jsonBehaviour.get("properties");
//                var condition = Conditions.createCondition(ResourceLocation.tryParse(id), nullableProperties);
//                runConditions.add(condition);
//            }
//        }
//    }
//
//    @Override
//    public boolean canRun(EventContext context) {
//        var mutatedContext = getMutatedContext(context);
//
//        if (mutatedContext == null)
//            return false;
//
//        if (!mutatedContext.isForced()) {
//            // Check if conditions to run are met
//            for (ICondition condition : runConditions) {
//                if (!condition.check(mutatedContext)) {
//                    return false;
//                }
//            }
//        }
//
//        // Check if all behaviours can run
//        for (var preActiveBehaviour : behaviours) {
//            // todo find a better way to check if behaviour can run
//            if (!preActiveBehaviour.create().canRun(mutatedContext)) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    @Override
//    public void execute(EventContext context) {
//        storedMutatedContext = getMutatedContext(context);
//
//        for (var preActiveBehaviour : behaviours) {
//            var behaviour = preActiveBehaviour.create();
//            behaviour.execute(storedMutatedContext);
//            activeBehaviours.add(behaviour);
//        }
//    }
//
//    @Override
//    public void dispose() {
//        for (IBehaviour behaviour : activeBehaviours) {
//            behaviour.dispose();
//        }
//    }
//
//    @Override
//    public boolean isDone() {
//        for (IBehaviour behaviour : this.activeBehaviours) {
//            if (!behaviour.isDone())
//                return false;
//        }
//        return true;
//    }
//}
