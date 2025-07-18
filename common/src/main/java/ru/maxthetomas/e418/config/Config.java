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
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.impl.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class Config {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<Value<?>> CONFIG_VALUES = new ArrayList<>();

    public static Value<Boolean> forceDebug = field("force_debug", Codec.BOOL, false);
    public static Value<Boolean> shouldSkipDebugScreen = field("skip_debug_screen", Codec.BOOL, true);
    public static Value<Set<ResourceLocation>> emptyWorlds = field("empty_worlds",
            ResourceLocation.CODEC.listOf().xmap(Set::copyOf, List::copyOf), Set.of(
                    E418.resLoc("lines"),
                    E418.resLoc("featureless_overworld"),
                    E418.resLoc("minimalism"),
                    E418.resLoc("unlabirynth")
            ));

    public static Value<Range> globalRandomEventDelay = field("global_random_event_delay", Range.CODEC.codec(), new Range(1200, 2400));
    public static Value<Range> globalRandomEventDelayFailure = field("global_random_event_delay_failure", Range.CODEC.codec(), new Range(600, 1200));

    public static Value<Float> playerRandomEventGroupDistance = field("player_random_event_group_distance", Codec.floatRange(0f, 50f), 50f);
    public static Value<Range> playerRandomEventDelay = field("player_random_event_delay", Range.CODEC.codec(), new Range(1200, 2400));
    public static Value<Range> playerRandomEventDelayFailure = field("player_random_event_delay", Range.CODEC.codec(), new Range(600, 1200));
    public static Value<Range> playerRandomEventOffset = field("player_random_event_offset", Range.CODEC.codec(), new Range(1200, 2400));
    public static Value<Range> playerRandomEventLock = field("player_random_event_lock", Range.CODEC.codec(), new Range(1200, 2400));


    public static boolean isDebug() {
        return Platform.isDevelopmentEnvironment() || forceDebug.get();
    }

    public static boolean shouldSkipBackupScreen() {
        return shouldSkipDebugScreen.get();
    }

    public static boolean isEmptyWorld(ResourceLocation location) {
        return emptyWorlds.get().contains(location);
    }


    private static <T> Value<T> field(String name, Codec<T> codec, T defaultValue) {
        var value = new Value<T>(name, defaultValue, codec);
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
        CONFIG_VALUES.forEach(v ->
                v.serializeAndAddToObject(object));
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
            result.ifSuccess(v -> {
                this.set(v.getFirst());
            });

            result.ifError((err) -> {
                LOGGER.error("Error while loading value {} from configuration file: {}", this.serializedName, err.toString());
            });


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
            serialized.ifSuccess(result -> {
                object.add(this.serializedName, result);
            });

            return serialized.isSuccess();
        }

        public T get() {
            return supplier.get();
        }
    }
}
