package ru.maxthetomas.e418.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.impl.ConstantNumberProvider;
import ru.maxthetomas.e418.codecs.impl.RandomNumberProvider;

import java.util.HashMap;
import java.util.Map;

public class NumberProviders {
    public static final Codec<NumberProvider> VALUE_CODEC =
            Codec.FLOAT.<NumberProvider>xmap(NumberProviders::staticProvider, a -> a.get(null, null).floatValue());

    private static final Map<ResourceLocation, MapCodec<? extends NumberProvider>> REGISTRY = new HashMap<>();

    public static final Codec<? extends NumberProvider> DISPATCH_CODEC = ResourceLocation.CODEC.dispatch(
            NumberProvider::getType, REGISTRY::get
    );

    public static final Codec<NumberProvider> CODEC = Codec.withAlternative(
            VALUE_CODEC,
            DISPATCH_CODEC
    );

    public static MapCodec<? extends NumberProvider> RANDOM = register(RandomNumberProvider.ID, RandomNumberProvider.CODEC);

    private static MapCodec<? extends NumberProvider> register(String id, MapCodec<? extends NumberProvider> codec) {
        return register(E418.resLoc(id), codec);
    }

    public static MapCodec<? extends NumberProvider> register(ResourceLocation location, MapCodec<? extends NumberProvider> codec) {
        REGISTRY.put(location, codec);
        return codec;
    }

    private static NumberProvider staticProvider(Number number) {
        return new ConstantNumberProvider(number);
    }
}
