package ru.maxthetomas.votvevents.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.impl.*;
import ru.maxthetomas.votvevents.event.EventContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A static {@linkplain ICondition} registry. Use <code>DISPATCH_CODEC</code> to create an instance of {@linkplain ICondition}.
 */
public class Conditions {
    public static Map<ResourceLocation, MapCodec<? extends ICondition>> REGISTRY = new HashMap<>();

    public static MapCodec<? extends ICondition> AT_HEIGHT = register(AtHeightCondition.ID, AtHeightCondition.CODEC);
    public static MapCodec<? extends ICondition> IS_NIGHT = register(IsNightCondition.ID, IsNightCondition.CODEC);
    public static MapCodec<? extends ICondition> WEATHER = register(WeatherCondition.ID, WeatherCondition.CODEC);
    public static MapCodec<? extends ICondition> PLAYERS_NEARBY = register(PlayersNearbyCondition.ID, PlayersNearbyCondition.CODEC);

    // Utility conditions
    public static MapCodec<? extends ICondition> ALWAYS = registerSimple("always", (ctx) -> true);
    public static MapCodec<? extends ICondition> NEVER = registerSimple("never", (ctx) -> false);
    public static MapCodec<? extends ICondition> OR = register(OrCondition.ID, OrCondition.CODEC);
    public static MapCodec<? extends ICondition> NOT = register(NotCondition.ID, NotCondition.CODEC);
    public static MapCodec<? extends ICondition> AND = register(AndCondition.ID, AndCondition.CODEC);
    public static MapCodec<? extends ICondition> RANDOM = register(RandomCondition.ID, RandomCondition.CODEC);
    public static MapCodec<? extends ICondition> DEBUG_MODE = registerSimple("debug_mode", (ctx) -> VotvEvents.getConfig().get().isDebug());


    public static Codec<ICondition> DISPATCH_CODEC = ResourceLocation.CODEC
            .dispatch(ICondition::getType, (s) -> REGISTRY.get(s));


    /**
     * Registers the condition into internal registry.
     *
     * @param key   The type of the codec.
     * @param codec The codec for condition.
     * @return The <code>codec</code> parameter.
     */
    public static MapCodec<? extends ICondition> register(ResourceLocation key, MapCodec<? extends ICondition> codec) {
        REGISTRY.put(key, codec);
        return codec;
    }

    /**
     * Registers a condition using a check function.
     */
    public static MapCodec<? extends ICondition> registerSimple(ResourceLocation location, Function<EventContext, Boolean> checkFunction) {
        var condition = new ICondition() {
            @Override
            public boolean check(EventContext context) {
                return checkFunction.apply(context);
            }

            @Override
            public ResourceLocation getType() {
                return location;
            }
        };

        var codec = MapCodec.<ICondition>unit(condition);
        return register(location, codec);
    }

    private static MapCodec<? extends ICondition> registerSimple(String type, Function<EventContext, Boolean> checkFunction) {
        return registerSimple(ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, type), checkFunction);
    }
}
