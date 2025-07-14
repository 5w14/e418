package ru.maxthetomas.e418.behaviour;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class ActiveBehaviourDispatch<T extends Behaviour> {
    public static Codec<Behaviour> DISPATCH_CODEC = ResourceLocation.CODEC.dispatch(Behaviour::getTypeId,
            v -> Behaviours.get(v).getOrThrow());

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
            Behaviour.BehaviourState.CODEC.forGetter(v -> v.state),
            DISPATCH_CODEC.fieldOf("data").forGetter(v -> v.activeBehaviour)
    ).apply(instance, (state, behaviour) -> {
        state.apply(behaviour);
        return create(state, behaviour);
    }));

}