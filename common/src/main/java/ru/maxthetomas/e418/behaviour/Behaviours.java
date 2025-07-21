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
    static final Map<ResourceLocation, MapCodec<? extends Behaviour>> STATE_CODEC_REGISTRY = new HashMap<>();

    public static final MapCodec<? extends Behaviour> BROADCAST_CHAT_MESSAGE =
            register(BroadcastMessageBehaviour.ID, BroadcastMessageBehaviour.CODEC, BroadcastMessageBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> DEBUG_PRINT_CONTEXT =
            register(DebugPrintContextBehaviour.ID, DebugPrintContextBehaviour.CODEC, DebugPrintContextBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> EXECUTE_COMMAND =
            register(ExecuteCommandBehaviour.ID, ExecuteCommandBehaviour.CODEC, ExecuteCommandBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> EXECUTE_EVENT =
            register(ExecuteEventBehaviour.ID, ExecuteEventBehaviour.CODEC, ExecuteEventBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> MODIFY_HELD_ITEM_COMPONENTS =
            register(ModifyHeldItemComponents.ID, ModifyHeldItemComponents.CODEC, ModifyHeldItemComponents.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> PLAY_SOUND =
            register(PlaySoundBehaviour.ID, PlaySoundBehaviour.CODEC, PlaySoundBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> TELEPORT_PLAYER =
            register(TeleportPlayerBehaviour.ID, TeleportPlayerBehaviour.CODEC, TeleportPlayerBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SHOW_TOAST =
            register(ShowToastBehaviour.ID, ShowToastBehaviour.CODEC, ShowToastBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SET_SHADER =
            register(SetShaderBehaviour.ID, SetShaderBehaviour.CODEC, SetShaderBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> GAME_CRASH =
            register(GameCrashBehaviour.ID, GameCrashBehaviour.CODEC, GameCrashBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SNOW_OVERRIDE =
            register(SnowOverrideBehaviour.ID, SnowOverrideBehaviour.CODEC, SnowOverrideBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> DISABLE_NIGHT_SKIP =
            register(DisableNightSkipBehaviour.ID, DisableNightSkipBehaviour.CODEC, DisableNightSkipBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> CANCEL_TIME_SKIP =
            register(CancelTimeSkipBehaviour.ID, CancelTimeSkipBehaviour.CODEC, CancelTimeSkipBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> BREAK_ATLAS =
            register(BreakAtlasBehaviour.ID, BreakAtlasBehaviour.CODEC, BreakAtlasBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> PREVENT_CHAT_USAGE =
            register(PreventChatUsageBehaviour.ID, PreventChatUsageBehaviour.CODEC, PreventChatUsageBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> META_PARANOIA =
            register(MetaParanoiaBehaviour.ID, MetaParanoiaBehaviour.CODEC, MetaParanoiaBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SET_SUN_TEXTURE =
            register(SetSunTextureBehaviour.ID, SetSunTextureBehaviour.CODEC, SetSunTextureBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SET_MOON_TEXTURE =
            register(SetMoonTextureBehaviour.ID, SetMoonTextureBehaviour.CODEC, SetMoonTextureBehaviour.STATE_CODEC);

    // Utilities
    public static final MapCodec<? extends Behaviour> MUTATE_CONTEXT =
            register(MutateContextBehaviour.ID, MutateContextBehaviour.CODEC, MutateContextBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> TIMEOUT =
            register(TimeoutBehaviour.ID, TimeoutBehaviour.CODEC, TimeoutBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> WAIT_FOR_CONDITIONS =
            register(WaitForConditionBehaviour.ID, WaitForConditionBehaviour.CODEC, WaitForConditionBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> STOP_EVENT =
            register(StopEventBehaviour.ID, StopEventBehaviour.CODEC, StopEventBehaviour.STATE_CODEC);

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
    public static MapCodec<? extends Behaviour> register(ResourceLocation id, MapCodec<? extends Behaviour> codec, MapCodec<? extends Behaviour> stateCodec) {
        REGISTRY.put(id, codec);
        STATE_CODEC_REGISTRY.put(id, stateCodec);
        return codec;
    }
}
