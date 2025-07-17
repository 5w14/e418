package ru.maxthetomas.e418.behaviour;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.behaviour.impl.*;

import java.util.HashMap;
import java.util.Map;

public class ActiveBehaviourDispatch<T extends Behaviour> {
    private static final Map<ResourceLocation, MapCodec<? extends Behaviour>> STATE_CODECS = new HashMap<>();

    public static Codec<Behaviour> DISPATCH_CODEC = Codec.lazyInitialized(() ->
            ResourceLocation.CODEC.dispatch(Behaviour::getTypeId, STATE_CODECS::get));

    public static final MapCodec<? extends Behaviour> BROADCAST_CHAT_MESSAGE = register(BroadcastMessageBehaviour.ID, BroadcastMessageBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> DEBUG_PRINT_CONTEXT = register(DebugPrintContextBehaviour.ID, DebugPrintContextBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> EXECUTE_COMMAND = register(ExecuteCommandBehaviour.ID, ExecuteCommandBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> EXECUTE_EVENT = register(ExecuteEventBehaviour.ID, ExecuteEventBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> MODIFY_HELD_ITEM_COMPONENTS = register(ModifyHeldItemComponents.ID, ModifyHeldItemComponents.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> PLAY_SOUND = register(PlaySoundBehaviour.ID, PlaySoundBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> TELEPORT_PLAYER = register(TeleportPlayerBehaviour.ID, TeleportPlayerBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SHOW_TOAST = register(ShowToastBehaviour.ID, ShowToastBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SET_SHADER = register(SetShaderBehaviour.ID, SetShaderBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> GAME_CRASH = register(GameCrashBehaviour.ID, GameCrashBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SNOW_OVERRIDE = register(SnowOverrideBehaviour.ID, SnowOverrideBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> DISABLE_NIGHT_SKIP = register(DisableNightSkipBehaviour.ID, DisableNightSkipBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> CANCEL_TIME_SKIP = register(CancelTimeSkipBehaviour.ID, CancelTimeSkipBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> BREAK_ATLAS = register(BreakAtlasBehaviour.ID, BreakAtlasBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> PREVENT_CHAT_USAGE = register(PreventChatUsageBehaviour.ID, PreventChatUsageBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> META_PARANOIA = register(MetaParanoiaBehaviour.ID, MetaParanoiaBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SET_SUN_TEXTURE = register(SetSunTextureBehaviour.ID, SetSunTextureBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> SET_MOON_TEXTURE = register(SetMoonTextureBehaviour.ID, SetMoonTextureBehaviour.STATE_CODEC);

    // Utilities
    public static final MapCodec<? extends Behaviour> MUTATE_CONTEXT = register(MutateContextBehaviour.ID, MutateContextBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> TIMEOUT = register(TimeoutBehaviour.ID, TimeoutBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> WAIT_FOR_CONDITIONS = register(WaitForConditionBehaviour.ID, WaitForConditionBehaviour.STATE_CODEC);
    public static final MapCodec<? extends Behaviour> STOP_EVENT = register(StopEventBehaviour.ID, StopEventBehaviour.STATE_CODEC);

    private static MapCodec<? extends Behaviour> register(ResourceLocation type, MapCodec<? extends Behaviour> stateCodec) {
        STATE_CODECS.put(type, stateCodec);
        return stateCodec;
    }


    Behaviour.BehaviourState state;
    T activeBehaviour;

    public T getActiveBehaviour() {
        return activeBehaviour;
    }

    public static <T extends Behaviour> ActiveBehaviourDispatch<T> create(Behaviour.BehaviourState state, T behaviour) {
        var abd = new ActiveBehaviourDispatch<T>();
        abd.activeBehaviour = behaviour;
        abd.state = state;
        return abd;
    }

    public static <T extends Behaviour> ActiveBehaviourDispatch<T> create(T behaviour) {
        var state = Behaviour.BehaviourState.create(behaviour);
        return create(state, behaviour);
    }

    public static MapCodec<ActiveBehaviourDispatch<Behaviour>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Behaviour.BehaviourState.CODEC.fieldOf("state")
                    .forGetter(v -> v.state),
            DISPATCH_CODEC.fieldOf("data").forGetter(v -> v.activeBehaviour)
    ).apply(instance, (state, behaviour) -> {
        state.apply(behaviour);
        return create(state, behaviour);
    }));

}