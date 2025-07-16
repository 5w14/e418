package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.event.cause.impl.WakeUpEventCause;

/// Cancels time skip
public class CancelTimeSkipBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "cancel_time_skip");
    public static final MapCodec<CancelTimeSkipBehaviour> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(CancelTimeSkipBehaviour::new));

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        var cause = context.getCause();
        if (cause instanceof WakeUpEventCause wakeUpCause) {
            wakeUpCause.cancelTimeSkip();
        }
    }
}

