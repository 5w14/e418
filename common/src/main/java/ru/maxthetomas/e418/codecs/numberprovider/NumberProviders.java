package ru.maxthetomas.e418.codecs.numberprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.codecs.numberprovider.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NumberProviders {
    public static final Codec<NumberProvider> VALUE_CODEC =
            Codec.FLOAT.xmap(NumberProviders::staticProvider, a -> a.get(null, null).floatValue());

    private static final Map<ResourceLocation, MapCodec<? extends NumberProvider>> REGISTRY = new HashMap<>();

    public static final Codec<? extends NumberProvider> DISPATCH_CODEC = ResourceLocation.CODEC.dispatch(
            NumberProvider::getType, REGISTRY::get
    );

    public static final Codec<NumberProvider> CODEC = Codec.withAlternative(
            VALUE_CODEC,
            DISPATCH_CODEC
    );

    public static MapCodec<? extends NumberProvider> RANDOM = register(RandomNumberProvider.ID, RandomNumberProvider.CODEC);
    public static MapCodec<? extends NumberProvider> CONFIG = register(ConfigNumberProvider.ID, ConfigNumberProvider.CODEC);

    public static MapCodec<? extends NumberProvider> register(ResourceLocation location, MapCodec<? extends NumberProvider> codec) {
        REGISTRY.put(location, codec);
        return codec;
    }

    private static NumberProvider staticProvider(Number number) {
        return new ConstantNumberProvider(number);
    }

    public static <T> RecordCodecBuilder<T, NumberProvider> codec(String fieldName, Function<T, NumberProvider> getter) {
        return CODEC.fieldOf(fieldName).forGetter(getter);
    }

    public static <T> RecordCodecBuilder<T, NumberProvider> codec(String fieldName,
            Number defaultValue, Function<T, NumberProvider> getter) {
        return CODEC.optionalFieldOf(fieldName, staticProvider(defaultValue)).forGetter(getter);
    }
}
