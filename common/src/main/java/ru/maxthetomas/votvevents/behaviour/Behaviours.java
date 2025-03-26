package ru.maxthetomas.votvevents.behaviour;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.impl.*;
import ru.maxthetomas.votvevents.behaviour.impl.context_mutator.RandomPlayerContextMutatorBehaviour;

import java.util.HashMap;

public class Behaviours {
    private static final HashMap<ResourceLocation, Builder> behaviours = new HashMap<>();

    public static final Builder EMPTY = register("empty", json -> ctx -> {
    });
    public static final Builder BROADCAST_CHAT_MESSAGE = register("broadcast_chat_message", BroadcastChatMessageBehaviour::new);
    public static final Builder PLAY_SOUND = register("play_sound", PlaySoundBehaviour::new);
    public static final Builder MAKE_CONSUMABLE = register("make_consumable", MakeConsumableBehaviour::new);
    public static final Builder TELEPORT_PLAYER = register("teleport_player", TeleportPlayerBehaviour::new);
    public static final Builder RANDOM_PLAYER_CONTEXT_MUTATOR = register("random_player_context_mutator", RandomPlayerContextMutatorBehaviour::new);
    public static final Builder DEBUG_PRINT_CONTEXT = register("debug_print_context", DebugPrintContextBehaviour::new);
    public static final Builder EXECUTE_COMMAND = register("execute_command", ExecuteCommandBehaviour::new);

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
