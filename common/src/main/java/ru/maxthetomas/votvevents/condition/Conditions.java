package ru.maxthetomas.votvevents.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.condition.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A static {@linkplain ICondition} registry. Use <code>DISPATCH_CODEC</code> to create an instance of {@linkplain ICondition}.
 */
public class Conditions {
    public static Map<ResourceLocation, MapCodec<? extends ICondition>> REGISTRY = new HashMap<>();

    public static MapCodec<? extends ICondition> ALWAYS = register(AlwaysCondition.ID, AlwaysCondition.CODEC);
    public static MapCodec<? extends ICondition> NEVER = register(NeverCondition.ID, NeverCondition.CODEC);
    public static MapCodec<? extends ICondition> DEBUG_MODE = register(DebugModeCondition.ID, DebugModeCondition.CODEC);
    public static MapCodec<? extends ICondition> AT_HEIGHT = register(AtHeightCondition.ID, AtHeightCondition.CODEC);
    public static MapCodec<? extends ICondition> IS_NIGHT = register(IsNightCondition.ID, IsNightCondition.CODEC);

    public static Codec<ICondition> DISPATCH_CODEC = ResourceLocation.CODEC
            .dispatch(ICondition::getType, (s) -> REGISTRY.get(s));


    /**
     * Registers the condition into internal registry.
     *
     * @param key   The type of the codec.
     * @param codec The codec for condition.
     * @return The <code>codec</code> parameter.
     */
    private static MapCodec<? extends ICondition> register(ResourceLocation key, MapCodec<? extends ICondition> codec) {
        REGISTRY.put(key, codec);
        return codec;
    }
}
