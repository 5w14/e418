package ru.maxthetomas.e418.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FloatRange(float min, float max) {
    public static final MapCodec<FloatRange> CODEC = RecordCodecBuilder.<FloatRange>mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("min").forGetter(FloatRange::min),
            Codec.FLOAT.fieldOf("max").forGetter(FloatRange::max)
    ).apply(instance, FloatRange::new)).validate(FloatRange::validate);

    public boolean isIn(float value) {
        return value < max && value > min;
    }

    public static DataResult<FloatRange> validate(FloatRange floatRange) {
        if (floatRange.min >= floatRange.max)
            return DataResult.error(() -> "Invalid range provided, min is bigger than max.");

        return DataResult.success(floatRange);
    }
}
