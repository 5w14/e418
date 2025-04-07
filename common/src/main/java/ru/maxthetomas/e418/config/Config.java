package ru.maxthetomas.e418.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("votvevents.json");
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean isDebug = false;
    private boolean shouldSkipBackupScreen;
    private int minTimeBetweenEvents = 20;
    private int maxTimeBetweenEvents = 200;
    private boolean isRandomEventsEnabled = false;
    private float wakeUpEventChance = 0.05f;
    private boolean isWakeUpEventsEnabled = true;

    /**
     * Reads config file and returns filled config
     *
     * @return filled config
     */
    public static Config loadConfig() {
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

    public boolean isDebug() {
        return Platform.isDevelopmentEnvironment() || isDebug;
    }

    public int getMinTimeBetweenEvents() {
        return minTimeBetweenEvents;
    }

    public int getMaxTimeBetweenEvents() {
        return maxTimeBetweenEvents;
    }

    public boolean isRandomEventsEnabled() {
        return isRandomEventsEnabled;
    }

    public float getWakeUpEventChance() {
        return wakeUpEventChance;
    }

    public boolean isWakeUpEventsEnabled() {
        return isWakeUpEventsEnabled;
    }

    public boolean shouldSkipBackupScreen() {
        return shouldSkipBackupScreen;
    }
}
