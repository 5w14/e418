package ru.maxthetomas.e418.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector4f;

public class ColorCodecs {
    public static Codec<Vector4f> STRING_CODEC = Codec.STRING.comapFlatMap(ColorCodecs::parseString, ColorCodecs::toString);
    public static Codec<Vector4f> CODEC = Codec.withAlternative(ExtraCodecs.VECTOR4F, STRING_CODEC);

    public static DataResult<Vector4f> parseString(String color) {
        if (color.startsWith("#")) {
            color = color.substring(1);
        }

        if (color.length() != 6 && color.length() != 8) {
            String c = color;
            return DataResult.error(() -> "Invalid color string (must be RRGGBB or RRGGBBAA): " + c);
        }

        int r = Integer.parseInt(color.substring(0, 2), 16);
        int g = Integer.parseInt(color.substring(2, 4), 16);
        int b = Integer.parseInt(color.substring(4, 6), 16);
        int a = (color.length() == 8)
                ? Integer.parseInt(color.substring(6, 8), 16)
                : 255;

        return DataResult.success(new Vector4f(r / 255f, g / 255f, b / 255f, a / 255f));
    }

    public static String toString(Vector4f color) {
        int r = Math.round(color.x * 255);
        int g = Math.round(color.y * 255);
        int b = Math.round(color.z * 255);
        int a = Math.round(color.w * 255);

        if (a >= 254) {
            return String.format("#%02X%02X%02X", r, g, b);
        }

        return String.format("#%02X%02X%02X%02X", r, g, b, a);
    }
}
