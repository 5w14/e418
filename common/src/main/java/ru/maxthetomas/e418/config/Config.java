package ru.maxthetomas.e418.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("e418.json");
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("is_debug").forGetter(Config::isDebug),
            Codec.BOOL.fieldOf("skip_backup_screen").forGetter(Config::shouldSkipBackupScreen),
            Codec.PASSTHROUGH.fieldOf("sources").forGetter(s -> s.sources)
    ).apply(instance, Config::new));
    private final boolean shouldSkipBackupScreen;
    private boolean isDebug = false;
    private Dynamic<?> sources;

    public Config(boolean isDebug, boolean shouldSkipBackupScreen, Dynamic<?> sources) {
        this.isDebug = isDebug;
        this.shouldSkipBackupScreen = shouldSkipBackupScreen;
        this.sources = sources;

        var map = sources.getMapValues().getOrThrow();
        var keys = map.keySet().stream().map(Dynamic::asString).map(DataResult::getOrThrow);
        keys.map(ResourceLocation::tryParse).filter(Objects::nonNull).forEach(v ->
                SourceConfigs.setValues(v, map.get(v.toString())));

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
                var config = new Config(false, true,
                        new Dynamic<>(JsonOps.INSTANCE).emptyMap());
                saveToFile(config);
                return config;
            }
        } catch (Exception exc) {
            LOGGER.error("Cannot load config!", exc);
        }

        return null;
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
            var jsonString = json.toString();
            Files.writeString(CONFIG_PATH, jsonString, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            LOGGER.error("Failed to save configuration", exception);
        }
    }

    private void updateSources() {
        sources = SourceConfigs.storeValues(
                new Dynamic<>(JsonOps.INSTANCE).emptyMap()
        );
    }

    public boolean isDebug() {
        return Platform.isDevelopmentEnvironment() || isDebug;
    }

    public boolean shouldSkipBackupScreen() {
        return shouldSkipBackupScreen;
    }
}
