package ru.maxthetomas.votvevents.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.behaviour.Behaviours;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.condition.ICondition;

import java.util.List;

/**
 * Event resource.
 */
public class EventResource {
    public static final MapCodec<EventResource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(EventResource::getName),
            Codec.STRING.optionalFieldOf("description", "").forGetter(EventResource::getDescription),
            Codec.PASSTHROUGH.listOf().fieldOf("behaviour"),

    ).apply(instance, EventResource::new));


    private List<PreActiveBehaviour> behaviourList;
    private List<ICondition> runConditions;
    private List<ICondition> queueConditions;

    private String name;
    private String description;

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<PreActiveBehaviour> getBehaviourList() {
        return behaviourList;
    }
    public List<Dynamic<JsonElement>> getBehaviourListAsDynamic() {
        return behaviourList.stream().map(PreActiveBehaviour::getBehaviourDataAsDynamic).toList();
    }

    public List<ICondition> getRunConditions() {
        return runConditions;
    }

    public List<ICondition> getQueueConditions() {
        return queueConditions;
    }

    public record PreActiveBehaviour(JsonElement behaviourData) {
        public Dynamic<JsonElement> getBehaviourDataAsDynamic() {
            return new Dynamic<>(JsonOps.INSTANCE, behaviourData);
        }

        public IBehaviour create() {
            return Behaviours.DISPATCH.parse(getBehaviourDataAsDynamic()).result().orElseThrow();
        }
    }

}
