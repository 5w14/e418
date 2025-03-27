package ru.maxthetomas.votvevents.behaviour;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.behaviour.impl.PlaySoundBehaviour;

import java.util.HashMap;
import java.util.Map;

public class Behaviours {
    private static final Map<ResourceLocation, MapCodec<? extends IBehaviour>> REGISTRY = new HashMap<>();
    public static Codec<? extends IBehaviour> DISPATCH = ResourceLocation.CODEC.dispatchStable(
            IBehaviour::getTypeId,
            (id) -> get(id).result().orElseThrow()
    );

    public static final MapCodec<? extends IBehaviour> PLAY_SOUND_BEHAVIOUR = register(PlaySoundBehaviour.ID, PlaySoundBehaviour.CODEC);

    public static DataResult<MapCodec<? extends IBehaviour>> get(ResourceLocation id) {
        if (REGISTRY.containsKey(id)) {
            return DataResult.success(REGISTRY.get(id));
        }

        return DataResult.error(() -> "Unknown behaviour: " + id);
    }

    public static MapCodec<? extends IBehaviour> register(ResourceLocation id, MapCodec<? extends IBehaviour> codec) {
        REGISTRY.put(id, codec);
        return codec;
    }
}
