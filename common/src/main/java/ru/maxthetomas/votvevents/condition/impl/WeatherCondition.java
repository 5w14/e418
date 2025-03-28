package ru.maxthetomas.votvevents.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

public class WeatherCondition implements ICondition {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "weather");
    public static final MapCodec<WeatherCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("weather").xmap(a -> Weather.valueOf(a.toUpperCase()), Enum::toString).forGetter(WeatherCondition::getWeather)
    ).apply(instance, WeatherCondition::new));

    private final Weather weather;

    public WeatherCondition(Weather weather) {
        this.weather = weather;
    }

    @Override
    public boolean check(EventContext context) {
        return switch (weather) {
            case RAIN -> context.getServer().overworld().isRaining();
            case THUNDER -> context.getServer().overworld().isThundering();
            default -> true;
        };
    }

    @Override
    public ResourceLocation getType() {
        return null;
    }

    public Weather getWeather() {
        return weather;
    }

    public enum Weather {
        CLEAR,
        RAIN,
        THUNDER
    }
}
