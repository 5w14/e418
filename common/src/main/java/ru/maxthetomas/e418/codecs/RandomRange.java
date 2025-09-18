package ru.maxthetomas.e418.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public record RandomRange(int min, int max) {
    public static final MapCodec<RandomRange> CODEC = RecordCodecBuilder.<RandomRange>mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("min").forGetter(RandomRange::min),
            Codec.INT.fieldOf("max").forGetter(RandomRange::max)
    ).apply(instance, RandomRange::new)).validate(RandomRange::validate);

    public static DataResult<RandomRange> validate(RandomRange r) {
        if (r.min <= r.max) {
            return DataResult.success(r);
        }
        return DataResult.error(() -> "Invalid range: Min value is larger than max");
    }

    public int randomValue(RandomSource random) {
        return random.nextInt(min, max);
    }
}

