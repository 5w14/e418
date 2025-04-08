package ru.maxthetomas.e418.codecs.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.NumberProvider;
import ru.maxthetomas.e418.codecs.NumberRequester;
import ru.maxthetomas.e418.event.EventContext;

public record RandomNumberProvider(float min, float max) implements NumberProvider {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(
            E418.MOD_ID, "random"
    );
    public static MapCodec<RandomNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("min").forGetter(RandomNumberProvider::min),
            Codec.FLOAT.fieldOf("max").forGetter(RandomNumberProvider::max)
    ).apply(instance, RandomNumberProvider::new));

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    @Override
    public Number get(EventContext context, NumberRequester requester) {
        var code = ((context.getSourceEvent().hashCode() + requester.hashCode()
                + context.getSourceEvent().startTime) << 8) ^ 0x24869;
        var source = RandomSource.create(code);
        return min + (source.nextFloat() * max - min);
    }
}
