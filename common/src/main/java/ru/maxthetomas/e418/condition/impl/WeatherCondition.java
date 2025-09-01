package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.util.Location;

/**
 * Returns true only when the required weather is present.
 * <ul>
 *   <li><code>weather</code> - Weather that will trigger.</li>
 * </ul>
 */
public record WeatherCondition(
        ru.maxthetomas.e418.condition.impl.WeatherCondition.Weather weather) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("weather");
    public static final MapCodec<WeatherCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Weather.CODEC.fieldOf("weather").forGetter(WeatherCondition::weather)
    ).apply(instance, WeatherCondition::new));

    @Override
    public boolean check(EventContext context) {
        return checkWeather(context);
    }

    boolean checkWeather(EventContext context) {
        if (context.getLocation() != null)
            return checkWeather(context.getLocation());
        else if (context.getPlayer() != null)
            return checkWeather(context.getPlayer());
        return checkWeather(context.getServer().overworld());
    }

    boolean checkWeather(Location location) {
        return checkWeather(location.level());
    }

    boolean checkWeather(ServerPlayer player) {
        return checkWeather(player.serverLevel());
    }

    boolean checkWeather(ServerLevel level) {
        return switch (weather) {
            case RAIN -> level.isRaining();
            case THUNDER -> level.isThundering();
            case ANY_NON_CLEAR -> level.getRainLevel(1F) > 0;
            case CLEAR -> level.getRainLevel(1F) < 0.2f;
        };
    }

    @Override
    public ResourceLocation getType() {
        return null;
    }

    public enum Weather implements StringRepresentable {
        CLEAR("clear"),
        RAIN("rain"),
        ANY_NON_CLEAR("any_non_clear"),
        THUNDER("thunder");

        public static final Codec<Weather> CODEC = StringRepresentable.fromEnum(Weather::values);

        final String name;

        Weather(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
