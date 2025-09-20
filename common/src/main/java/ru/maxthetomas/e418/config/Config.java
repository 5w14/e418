package ru.maxthetomas.e418.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import ru.maxthetomas.e418.codecs.RandomRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class Config {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<Value<?>> CONFIG_VALUES = new ArrayList<>();

    public static final Value<Boolean> forceDebug = field("force_debug", Codec.BOOL, false);
    public static final Value<Boolean> shouldSkipDebugScreen = field("skip_debug_screen", Codec.BOOL, true);
    public static final Value<Boolean> allowGameCrashes = field("allow_game_crashes", Codec.BOOL, true);
    public static final Value<Set<ResourceLocation>> emptyWorlds = field("empty_worlds", ResourceLocation.CODEC.listOf().xmap(Set::copyOf, List::copyOf), Set.of());
    public static final Value<Set<String>> hiddenNamespaces = field("command_hidden_namespaces", Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf), Set.of("e418"));

    public static final Value<Float> wakeUpEventChance = field("wake_up_event_chance", Codec.floatRange(0f, 1f), 0.1f);

    public static final Value<RandomRange> globalRandomEventDelay = field("global_random_event_delay", RandomRange.CODEC.codec(), new RandomRange(1200, 2400));
    public static final Value<RandomRange> globalRandomEventDelayFailure = field("global_random_event_delay_failure", RandomRange.CODEC.codec(), new RandomRange(600, 1200));
    public static final Value<RandomRange> globalRandomEventGracePeriod = field("player_random_event_grace_period", RandomRange.CODEC.codec(), new RandomRange(1200, 2400));

    public static final Value<Float> playerRandomEventGroupDistance = field("player_random_event_group_distance", Codec.floatRange(0f, 50f), 50f);
    public static final Value<RandomRange> playerRandomEventDelay = field("player_random_event_delay", RandomRange.CODEC.codec(), new RandomRange(1200, 2400));
    public static final Value<RandomRange> playerRandomEventDelayFailure = field("player_random_event_delay", RandomRange.CODEC.codec(), new RandomRange(600, 1200));
    public static final Value<RandomRange> playerRandomEventOffset = field("player_random_event_offset", RandomRange.CODEC.codec(), new RandomRange(1200, 2400));
    public static final Value<RandomRange> playerRandomEventLock = field("player_random_event_lock", RandomRange.CODEC.codec(), new RandomRange(1200, 2400));
    public static final Value<RandomRange> playerRandomEventGracePeriod = field("player_random_event_grace_period", RandomRange.CODEC.codec(), new RandomRange(1200, 2400));


    public static boolean isDebug() {
        return Platform.isDevelopmentEnvironment() || forceDebug.get();
    }

    public static boolean shouldSkipBackupScreen() {
        return shouldSkipDebugScreen.get();
    }

    public static boolean isEmptyWorld(ResourceLocation location) {
        return emptyWorlds.get().contains(location);
    }

    public static boolean shouldHideNamespace(ResourceLocation location) {
        var ns = location.getNamespace();
        return hiddenNamespaces.get().contains(ns);
    }


    private static <T> Value<T> field(String name, Codec<T> codec, T defaultValue) {
        var value = new Value<>(name, defaultValue, codec);
        CONFIG_VALUES.add(value);
        return value;
    }

    /**
     * Deserializes a JSON object and sets config values.
     */
    public static boolean deserializeConfig(JsonObject object) {
        return CONFIG_VALUES.stream().allMatch(v ->
                v.deserializeFromMapAndUpdate(object));
    }

    /**
     * Serializes a config into a JSON object.
     */
    public static JsonObject serializeConfig() {
        var object = new JsonObject();

        var failed = CONFIG_VALUES.stream().filter(v -> !v.serializeAndAddToObject(object)).toList();
        if (!failed.isEmpty()) {
            LOGGER.warn("Could not serialize {} value(s) during config serialization.", failed.size());
        }

        return object;
    }

    public static class Value<T> {
        private final String serializedName;
        private final Codec<T> serializer;
        private final T defaultValue;

        private Supplier<T> supplier;

        public Value(String serializedName, T defValue, Codec<T> serializer) {
            this.serializedName = serializedName;
            this.supplier = () -> defValue;
            this.serializer = serializer;
            this.defaultValue = defValue;
        }

        public Codec<T> getSerializer() {
            return serializer;
        }

        /**
         * Sets a value.
         */
        public void set(T value) {
            supplier = () -> value;
        }

        /**
         * Tries to deserialize the element.
         *
         * @return whether the deserialization was successful.
         */
        public boolean deserializeAndUpdate(JsonElement element) {
            var result = this.getSerializer().decode(JsonOps.INSTANCE, element);
            result.ifSuccess(v -> this.set(v.getFirst()));

            result.ifError((err) -> LOGGER.error("Error while loading value {} from configuration file: {}", this.serializedName, err.toString()));


            return result.isSuccess();
        }

        /**
         * Tries to find in a given object and deserialize the element.
         *
         * @return whether the deserialization was successful.
         */
        public boolean deserializeFromMapAndUpdate(JsonObject object) {
            var field = object.get(serializedName);
            if (field == null) {
                this.set(this.defaultValue);
                return false;
            }

            return this.deserializeAndUpdate(field);
        }

        public DataResult<JsonElement> serialize() {
            return this.getSerializer().encode(this.get(), JsonOps.INSTANCE, JsonOps.INSTANCE.empty());
        }

        public boolean serializeAndAddToObject(JsonObject object) {
            var serialized = this.serialize();
            serialized.ifSuccess(result -> object.add(this.serializedName, result));

            return serialized.isSuccess();
        }

        public T get() {
            return supplier.get();
        }
    }
}
