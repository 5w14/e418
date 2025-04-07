package ru.maxthetomas.e418.behaviour;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.ResourceLocation;

/**
 * This class represents behaviours before they are activated. Use {@code create()} to convert into an active behaviour.
 *
 * @see Behaviour
 */
public class PreActiveBehaviour {
    public static Codec<PreActiveBehaviour> CODEC = new Codec<PreActiveBehaviour>() {
        // The encoding of the type and the rest of the data
        @Override
        public <T> DataResult<T> encode(PreActiveBehaviour input, DynamicOps<T> ops, T prefix) {
            T typeValue = ResourceLocation.CODEC.encode(input.type, ops, ops.empty()).getOrThrow();

            // Create a map with the type field
            T result = ops.mergeToMap(prefix, ops.createString("type"), typeValue).getOrThrow();

            // The rest of the data is already in Dynamic format, so we can merge it directly
            if (input.data.getOps() == ops) {
                @SuppressWarnings("unchecked")
                T dataValue = (T) input.data.getValue();
                result = ops.mergeToMap(result, dataValue, prefix).getOrThrow();
            }

            return DataResult.success(result);
        }

        // Decoding into the type and the rest of the data
        @Override
        public <T> DataResult<Pair<PreActiveBehaviour, T>> decode(DynamicOps<T> dynamicOps, T t) {
            // We get a type from the input
            var type = dynamicOps.get(t, "type");

            // No type - bye bye
            if (type.isError()) {
                return DataResult.error(() -> "Could not get `type` from input!");
            }

            // Decode as string
            var typeString = ResourceLocation.CODEC.decode(dynamicOps, type.getOrThrow());
            // not string - also bye bye
            if (typeString.isError()) {
                return DataResult.error(() -> "Could not get `type` from input!");
            }

            // Create a new PreCreatedClassInlined with the type and the rest of the data
            var c = new PreActiveBehaviour(typeString.getOrThrow().getFirst(), new Dynamic<T>(dynamicOps, t));
            return DataResult.success(Pair.of(c, t));
        }
    };

    private final ResourceLocation type;
    private final Dynamic<?> data;

    public PreActiveBehaviour(ResourceLocation type, Dynamic<?> data) {
        this.type = type;
        this.data = data;

        // Ensure that the type is registered
        Behaviours.get(type).getOrThrow();
    }

    public ResourceLocation getType() {
        return type;
    }

    public Dynamic<?> getData() {
        return data;
    }

    /**
     * Creates a new instance of {@link Behaviour}, after parsing the stored data.
     */
    public Behaviour create() {
        return Behaviours.get(type).getOrThrow().decoder()
                .decode(data).result().orElseThrow().getFirst();
    }
}
