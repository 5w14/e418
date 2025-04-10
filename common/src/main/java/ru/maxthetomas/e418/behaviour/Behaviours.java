package ru.maxthetomas.e418.behaviour;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.behaviour.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the {@linkplain Behaviour} registry.
 * Every behaviour registers a {@linkplain com.mojang.serialization.MapCodec}, which is used for their deserialization.
 */
public class Behaviours {
    private static final Map<ResourceLocation, MapCodec<? extends Behaviour>> REGISTRY = new HashMap<>();

    public static final MapCodec<? extends Behaviour> BROADCAST_CHAT_MESSAGE = register(BroadcastMessageBehaviour.ID, BroadcastMessageBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> DEBUG_PRINT_CONTEXT = register(DebugPrintContextBehaviour.ID, DebugPrintContextBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> EXECUTE_COMMAND = register(ExecuteCommandBehaviour.ID, ExecuteCommandBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> EXECUTE_EVENT = register(ExecuteEventBehaviour.ID, ExecuteEventBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> MODIFY_HELD_ITEM_COMPONENTS = register(ModifyHeldItemComponents.ID, ModifyHeldItemComponents.CODEC);
    public static final MapCodec<? extends Behaviour> PLAY_SOUND = register(PlaySoundBehaviour.ID, PlaySoundBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> TELEPORT_PLAYER = register(TeleportPlayerBehaviour.ID, TeleportPlayerBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> SHOW_TOAST = register(ShowToastBehaviour.ID, ShowToastBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> SET_SHADER = register(SetShaderBehaviour.ID, SetShaderBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> GAME_CRASH = register(GameCrashBehaviour.ID, GameCrashBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> SNOW_OVERRIDE = register(SnowOverrideBehaviour.ID, SnowOverrideBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> DISABLE_NIGHT_SKIP = register(DisableNightSkipBehaviour.ID, DisableNightSkipBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> CANCEL_TIME_SKIP = register(CancelTimeSkipBehaviour.ID, CancelTimeSkipBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> BREAK_ATLAS = register(BreakAtlasBehaviour.ID, BreakAtlasBehaviour.CODEC);

    // Utilities
    public static final MapCodec<? extends Behaviour> MUTATE_CONTEXT = register(MutateContextBehaviour.ID, MutateContextBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> TIMEOUT = register(TimeoutBehaviour.ID, TimeoutBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> WAIT_FOR_CONDITIONS = register(WaitForConditionBehaviour.ID, WaitForConditionBehaviour.CODEC);
    public static final MapCodec<? extends Behaviour> STOP_EVENT = register(StopEventBehaviour.ID, StopEventBehaviour.CODEC);

    /**
     * Gets a codec to create a {@linkplain Behaviour} from the registry.
     *
     * @param id {@linkplain Behaviour}'s id.
     * @return A {@linkplain DataResult} with either a codec, or an error, if the behaviour is not found.
     */
    public static DataResult<MapCodec<? extends Behaviour>> get(ResourceLocation id) {
        if (REGISTRY.containsKey(id)) {
            return DataResult.success(REGISTRY.get(id));
        }

        return DataResult.error(() -> "Unknown behaviour: " + id);
    }


    /**
     * Register the codec for {@linkplain Behaviour} into internal registry.
     * Used within static contexts, before mod initialization.
     *
     * @param id    The id to register the codec to.
     * @param codec The codec to register.
     * @return The second param, {@code codec}.
     */
    public static MapCodec<? extends Behaviour> register(ResourceLocation id, MapCodec<? extends Behaviour> codec) {
        REGISTRY.put(id, codec);
        return codec;
    }
}
