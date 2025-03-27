package ru.maxthetomas.votvevents.behaviour;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.behaviour.impl.*;

import java.util.HashMap;
import java.util.Map;

public class Behaviours {
    private static final Map<ResourceLocation, MapCodec<? extends IBehaviour>> REGISTRY = new HashMap<>();

    public static final MapCodec<? extends IBehaviour> BROADCAST_CHAT_MESSAGE = register(BroadcastChatMessageBehaviour.ID, BroadcastChatMessageBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> DEBUG_PRINT_CONTEXT = register(DebugPrintContextBehaviour.ID, DebugPrintContextBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> EXECUTE_COMMAND = register(ExecuteCommandBehaviour.ID, ExecuteCommandBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> MAKE_CONSUMABLE = register(MakeConsumableBehaviour.ID, MakeConsumableBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> PLAY_SOUND = register(PlaySoundBehaviour.ID, PlaySoundBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> TELEPORT_PLAYER = register(TeleportPlayerBehaviour.ID, TeleportPlayerBehaviour.CODEC);

    public static DataResult<MapCodec<? extends IBehaviour>> get(ResourceLocation id) {
        if (REGISTRY.containsKey(id)) {
            return DataResult.success(REGISTRY.get(id));
        }

        return DataResult.error(() -> "Unknown behaviour: " + id);
    }

    public static MapCodec<? extends IBehaviour> register(ResourceLocation id, MapCodec<? extends IBehaviour> codec) {
        REGISTRY.put(id, codec);
        return codec;
    }
}
