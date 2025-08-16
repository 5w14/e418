package ru.maxthetomas.e418.event.cause.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.cause.IEventCause;

/**
 * Cause when event was caused by sleeping.
 */
public class WakeUpEventCause implements IEventCause {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "wake_up");
    public static final MapCodec<WakeUpEventCause> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("skip_cancelled").forGetter(v -> v.isTimeSkipCancelled)
    ).apply(instance, WakeUpEventCause::new));

    private boolean isTimeSkipCancelled = false;

    /**
     * @return Is time skip was cancelled
     */
    public boolean isTimeSkipCancelled() {
        return isTimeSkipCancelled;
    }

    /**
     * Cancels time skip after sleeping
     */
    public void cancelTimeSkip() {
        isTimeSkipCancelled = true;
    }

    public WakeUpEventCause() {
    }

    public WakeUpEventCause(boolean isCancelled) {
        this.isTimeSkipCancelled = isCancelled;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }
}
