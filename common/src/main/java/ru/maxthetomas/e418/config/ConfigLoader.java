package ru.maxthetomas.e418.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("e418.json");

    public static void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                return;
            }

            var jsonString = Files.readString(CONFIG_PATH);
            var configJson = JsonParser.parseString(jsonString);
            if (!configJson.isJsonObject()) {
                throw new RuntimeException("Invalid configuration!");
            }

            var result = Config.deserializeConfig(configJson.getAsJsonObject());
            if (!result) {
                LOGGER.warn("Error while config deserialization!");
            }
        } catch (IOException e) {
            LOGGER.error("Could not load config!", e);
        }
    }

    public static void saveConfig() {
        try {
            var result = Config.serializeConfig();
            Files.writeString(CONFIG_PATH, GSON.toJson(result));
        } catch (IOException e) {
            LOGGER.error("Could not save config!", e);
        }
    }

}
