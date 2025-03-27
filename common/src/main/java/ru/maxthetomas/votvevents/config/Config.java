package ru.maxthetomas.votvevents.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private boolean isDebug = false;
    private String testVar = "AAA NULL";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("votvevents.json");
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    public boolean isDebug() {
        return Platform.isDevelopmentEnvironment() || isDebug;
    }

    /**
     * Reads config file and returns filled config
     *
     * @return filled config
     */
    public static Config getConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    Config config = GSON.fromJson(JsonParser.parseReader(reader), Config.class);
                    saveToConfig(config);
                    return config;
                }
            } else {
                saveToConfig(new Config());
                return new Config();
            }
        } catch (Exception exc) {
            LOGGER.error("Failed to load configuration", exc);
            saveToConfig(new Config());
            return new Config();
        }
    }

    /**
     * Saves config to file
     *
     * @param config instance to save
     */
    public static void saveToConfig(Config config) {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(config, writer);
        } catch (IOException exception) {
            LOGGER.error("Failed to save configuration", exception);
        }
    }
}
