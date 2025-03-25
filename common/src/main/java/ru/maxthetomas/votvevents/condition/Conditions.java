package ru.maxthetomas.votvevents.condition;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.impl.IsNightCondition;
import ru.maxthetomas.votvevents.config.Config;

import java.util.HashMap;

public class Conditions {
    private static final HashMap<ResourceLocation, Builder> conditions = new HashMap<>();

    public static final Builder ALWAYS = register("always", (json) -> (ctx) -> true);
    public static final Builder NEVER = register("never", (json) -> (ctx) -> false);
    public static final Builder DEBUG_MODE = register("debug_mode", (json) -> (ctx) -> Config.isDebug());
    public static final Builder IS_NIGHT = register("is_night", IsNightCondition::new);

    public static ICondition createCondition(ResourceLocation name, JsonElement jsonObject) {
        return getConditionBuilder(name).apply(jsonObject);
    }

    public static Builder getConditionBuilder(ResourceLocation name) {
        return conditions.get(name);
    }

    public static Builder registerCondition(ResourceLocation name, Builder builder) {
        conditions.put(name, builder);
        return builder;
    }

    private static Builder register(String name, Builder builder) {
        return registerCondition(ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, name), builder);
    }

    @FunctionalInterface
    public interface Builder {
        ICondition apply(JsonElement jsonElement);
    }
}
