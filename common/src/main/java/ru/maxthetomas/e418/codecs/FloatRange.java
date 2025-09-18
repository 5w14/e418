package ru.maxthetomas.e418.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FloatRange(float min, float max) {
    public static final MapCodec<FloatRange> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("min").forGetter(FloatRange::min),
            Codec.FLOAT.fieldOf("max").forGetter(FloatRange::max)
    ).apply(instance, FloatRange::new));

    public boolean isIn(float value) {
        return value < max && value > min;
    }
}
