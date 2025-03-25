package ru.maxthetomas.votvevents.behaviour;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.impl.BroadcastChatMessageBehaviour;

import java.util.HashMap;

public class Behaviours {
    private static final HashMap<ResourceLocation, Builder> behaviours = new HashMap<>();

    public static final Builder EMPTY = register("empty", json -> ctx -> {
    });
    public static final Builder BROADCAST_CHAT_MESSAGE = register("broadcast_chat_message", BroadcastChatMessageBehaviour::new);

    public static IBehaviour createBehaviour(ResourceLocation name, JsonElement jsonObject) {
        return getBehaviourBuilder(name).apply(jsonObject);
    }

    public static Builder getBehaviourBuilder(ResourceLocation name) {
        return behaviours.get(name);
    }

    public static Builder registerBehaviour(ResourceLocation name, Builder builder) {
        behaviours.put(name, builder);
        return builder;
    }

    private static Builder register(String name, Builder builder) {
        return registerBehaviour(ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, name), builder);
    }

    @FunctionalInterface
    public interface Builder {
        IBehaviour apply(JsonElement jsonElement);
    }
}
