package ru.maxthetomas.e418.codecs;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector4f;

public class FogCodecs {
    public static final MapCodec<FogConfig> CODEC = RecordCodecBuilder.<FogConfig>mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("start", 32f).forGetter(FogConfig::start),
            Codec.FLOAT.optionalFieldOf("end", 96f).forGetter(FogConfig::end),
            Codec.INT.optionalFieldOf("shape", 0).forGetter(FogConfig::shape),
            ColorCodecs.CODEC.fieldOf("color").forGetter(FogConfig::color)
    ).apply(instance, FogConfig::fromCodec));

    public static final StreamCodec<RegistryFriendlyByteBuf, FogConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, FogConfig::start,
            ByteBufCodecs.FLOAT, FogConfig::end,
            ByteBufCodecs.INT, FogConfig::shape,
            ByteBufCodecs.FLOAT, FogConfig::red,
            ByteBufCodecs.FLOAT, FogConfig::green,
            ByteBufCodecs.FLOAT, FogConfig::blue,
            ByteBufCodecs.FLOAT, FogConfig::alpha,
            FogConfig::new
    );

    /**
     * Serializable fog override
     */
    public record FogConfig(float start, float end, int shape,
                            float red, float green, float blue, float alpha) {
        public static FogConfig EMPTY = new FogConfig(0, 0, 0, 0, 0, 0, 0);

        public static FogConfig fromCodec(float start, float end, int shape, Vector4f color) {
            return new FogConfig(start, end, shape, color.x, color.y, color.z, color.w);
        }

        public Vector4f color() {
            return new Vector4f(red, green, blue, alpha);
        }

        @Environment(EnvType.CLIENT)
        public FogParameters convert() {
            return new FogParameters(start, end, shape == 1 ? FogShape.CYLINDER :
                    FogShape.SPHERE, red, green, blue, alpha);
        }
    }
}
