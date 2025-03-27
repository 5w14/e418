package ru.maxthetomas.votvevents.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class Conditions {
    private static final Map<ResourceLocation, ICondition> REGISTRY = new HashMap<>();
    public static Codec<ICondition> CODEC = ResourceLocation.CODEC.dispatch(ICondition::getTypeId,
            (id) -> get(id).result().orElseThrow().getType());

    public static DataResult<ICondition> get(ResourceLocation id) {
        if (REGISTRY.containsKey(id)) {
            return DataResult.success(REGISTRY.get(id));
        }

        return DataResult.error(() -> "Unknown condition: " + id);
    }

}
