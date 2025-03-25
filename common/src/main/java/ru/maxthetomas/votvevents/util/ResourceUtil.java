package ru.maxthetomas.votvevents.util;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.stream.Collectors;

public class ResourceUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static JsonElement getJsonResource(ResourceManager resourceManager, ResourceLocation location) {
        var result = resourceManager.getResource(location);

        if (result.isEmpty()) {
            return null;
        }

        var resource = result.get();
        try(var reader = resource.openAsReader()) {
            var data = reader.lines().parallel().collect(Collectors.joining("\n"));
            return JsonParser.parseString(data);
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.warn("Failed to parse JSON resource at location {}", location, e);
            return null;
        }
    }
}
