package ru.maxthetomas.votvevents.behaviour.impl.context_mutators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.behaviour.impl.context_mutators.impl.SelectRandomPlayerContextMutator;

import java.util.HashMap;
import java.util.Map;

public class ContextMutators {
    private static final Map<ResourceLocation, MapCodec<? extends IContextMutator>> REGISTRY = new HashMap<>();

    public static final MapCodec<? extends IContextMutator> SELECT_RANDOM_PLAYER = register(SelectRandomPlayerContextMutator.ID, SelectRandomPlayerContextMutator.CODEC);

    public static DataResult<MapCodec<? extends IContextMutator>> get(ResourceLocation id) {
        if (REGISTRY.containsKey(id)) {
            return DataResult.success(REGISTRY.get(id));
        }

        return DataResult.error(() -> "Unknown mutator: " + id);
    }

    public static MapCodec<? extends IContextMutator> register(ResourceLocation id, MapCodec<? extends IContextMutator> codec) {
        REGISTRY.put(id, codec);
        return codec;
    }

    public static Codec<IContextMutator> DISPATCH_CODEC = ResourceLocation.CODEC
            .dispatch(IContextMutator::getType, REGISTRY::get);
}
