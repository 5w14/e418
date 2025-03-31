package ru.maxthetomas.votvevents.behaviour;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.behaviour.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the {@linkplain IBehaviour} registry.
 * Every behaviour registers a {@linkplain com.mojang.serialization.MapCodec}, which is used for their deserialization.
 */
public class Behaviours {
    private static final Map<ResourceLocation, MapCodec<? extends IBehaviour>> REGISTRY = new HashMap<>();

    public static final MapCodec<? extends IBehaviour> BROADCAST_CHAT_MESSAGE = register(BroadcastChatMessageBehaviour.ID, BroadcastChatMessageBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> DEBUG_PRINT_CONTEXT = register(DebugPrintContextBehaviour.ID, DebugPrintContextBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> EXECUTE_COMMAND = register(ExecuteCommandBehaviour.ID, ExecuteCommandBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> MODIFY_HELD_ITEM_COMPONENTS = register(ModifyHeldItemComponents.ID, ModifyHeldItemComponents.CODEC);
    public static final MapCodec<? extends IBehaviour> PLAY_SOUND = register(PlaySoundBehaviour.ID, PlaySoundBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> TELEPORT_PLAYER = register(TeleportPlayerBehaviour.ID, TeleportPlayerBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> MUTATE_CONTEXT = register(MutateContextBehaviour.ID, MutateContextBehaviour.CODEC);
    public static final MapCodec<? extends IBehaviour> WHILE = register(WhileBehaviour.ID, WhileBehaviour.CODEC);

    /**
     * Gets a codec to create a {@linkplain IBehaviour} from the registry.
     *
     * @param id {@linkplain IBehaviour}'s id.
     * @return A {@linkplain DataResult} with either a codec, or an error, if the behaviour is not found.
     */
    public static DataResult<MapCodec<? extends IBehaviour>> get(ResourceLocation id) {
        if (REGISTRY.containsKey(id)) {
            return DataResult.success(REGISTRY.get(id));
        }

        return DataResult.error(() -> "Unknown behaviour: " + id);
    }


    /**
     * Register the codec for {@linkplain IBehaviour} into internal registry.
     * Used within static contexts, before mod initialization.
     *
     * @param id    The id to register the codec to.
     * @param codec The codec to register.
     * @return The second param, {@code codec}.
     */
    public static MapCodec<? extends IBehaviour> register(ResourceLocation id, MapCodec<? extends IBehaviour> codec) {
        REGISTRY.put(id, codec);
        return codec;
    }
}
