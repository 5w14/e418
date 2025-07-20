package ru.maxthetomas.e418.codecs.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public record Range(int min, int max) {
    public static MapCodec<Range> CODEC = RecordCodecBuilder.<Range>mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("min").forGetter(Range::min),
            Codec.INT.fieldOf("max").forGetter(Range::max)
    ).apply(instance, Range::new)).validate(Range::validate);

    public static DataResult<Range> validate(Range r) {
        if (r.min <= r.max) {
            return DataResult.success(r);
        }
        return DataResult.error(() -> "Invalid range: Min value is larger than max");
    }

    public int randomValue(RandomSource random) {
        return random.nextInt(min, max);
    }
}

