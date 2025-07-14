package ru.maxthetomas.e418.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.registry.EventRegistries;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("e418.json");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("is_debug").forGetter(Config::isDebug),
            Codec.BOOL.fieldOf("skip_backup_screen").forGetter(Config::shouldSkipBackupScreen),
            ResourceLocation.CODEC.listOf().lenientOptionalFieldOf("empty_worlds", List.of())
                    .forGetter(Config::getEmptyWorldsAsList),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.PASSTHROUGH)
                    .optionalFieldOf("sources").forGetter(s -> Optional.of(s.sources))
    ).apply(instance, Config::new));

    private final boolean shouldSkipBackupScreen;
    private final Set<ResourceLocation> emptyWorlds;
    private boolean isDebug = false;
    private final Map<ResourceLocation, Dynamic<?>> sources;

    public Config(boolean isDebug, boolean shouldSkipBackupScreen, List<ResourceLocation> emptyWorlds, Optional<Map<ResourceLocation, Dynamic<?>>> registryDynamics) {
        this.isDebug = isDebug;
        this.shouldSkipBackupScreen = shouldSkipBackupScreen;
        this.emptyWorlds = new HashSet<>(emptyWorlds);
        this.sources = registryDynamics.orElse(new HashMap<>());

        this.sources.forEach((key, value) -> {
            EventRegistries.get(key).get().getConfig().setValues(value);
        });

        updateSources();
    }

    /**
     * Reads config file and returns filled config
     *
     * @return filled config
     */
    public static Config loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                var reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8);
                var json = JsonParser.parseReader(reader);
                reader.close();
                var config = CODEC.decode(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(json).getOrThrow());
                return config.getOrThrow();
            } else {
                var config = empty();
                saveToFile(config);
                return config;
            }
        } catch (Exception exc) {
            LOGGER.error("Cannot load config!", exc);
        }

        return empty();
    }

    /**
     * Saves config to file
     *
     * @param config instance to save
     */
    public static void saveToFile(Config config) {
        try {
            var record = CODEC.encode(config, JsonOps.INSTANCE, JsonOps.INSTANCE.mapBuilder());
            var json = record.build(JsonOps.INSTANCE.empty()).getOrThrow();
            Files.writeString(CONFIG_PATH, GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            LOGGER.error("Failed to save configuration", exception);
        }
    }

    private static Config empty() {
        return new Config(false, true, List.of(
                ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "lines"),
                ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "featureless_overworld"),
                ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "minimalism"),
                ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "unlabirynth")
        ), Optional.of(Map.of()));
    }

    private void updateSources() {
        for (ResourceLocation registryId : EventRegistries.getRegistries()) {
            var registry = EventRegistries.get(registryId);
            sources.compute(registryId, (a, b) -> registry.get().getConfig().storeValues(
                    new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.emptyMap())
            ));
        }
    }

    public boolean isDebug() {
        return Platform.isDevelopmentEnvironment() || isDebug;
    }

    public boolean shouldSkipBackupScreen() {
        return shouldSkipBackupScreen;
    }

    public Set<ResourceLocation> getEmptyWorlds() {
        return emptyWorlds;
    }

    public List<ResourceLocation> getEmptyWorldsAsList() {
        return emptyWorlds.stream().toList();
    }

    public boolean isEmptyWorld(ResourceLocation resourceLocation) {
        return getEmptyWorlds().contains(resourceLocation);
    }
}
