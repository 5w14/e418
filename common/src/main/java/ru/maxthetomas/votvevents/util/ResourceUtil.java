package ru.maxthetomas.votvevents.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import ru.maxthetomas.votvevents.VotvEvents;

import java.io.IOException;
import java.util.stream.Collectors;

public class ResourceUtil {
    public static JsonElement getJsonResource(ResourceManager resourceManager, String path) {
        var result = resourceManager.getResource(ResourceLocation.tryBuild(VotvEvents.MOD_ID, path));

        if (result.isEmpty()) {
            return null;
        }

        var resource = result.get();
        try(var reader = resource.openAsReader()) {
            var data = reader.lines().parallel().collect(Collectors.joining("\n"));
            return JsonParser.parseString(data);
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
