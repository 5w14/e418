package ru.maxthetomas.e418.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;

import java.util.Optional;

public record QueuedEvent(EventResource resource, EventContext context, @Nullable Long timeoutTick) {
    public static final MapCodec<QueuedEvent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.xmap(v -> E418.getEventManager().getEvent(v),
                            res -> E418.getEventManager().getResourceLocation(res)).fieldOf("id")
                    .forGetter(v -> v.resource),
            EventContext.CODEC.fieldOf("context").forGetter(v -> v.context),
            Codec.LONG.lenientOptionalFieldOf("timeout_tick").forGetter(v -> Optional.ofNullable(v.timeoutTick))
    ).apply(i, QueuedEvent::constructFromCodec));

    @Override
    public String toString() {
        if (timeoutTick != null)
            return String.format("QueuedEvent[%s] (times out at [%s])", resource.name(), timeoutTick);

        return String.format("QueuedEvent[%s] (no timeout)", resource.name());
    }

    public static QueuedEvent constructFromCodec(EventResource resource, EventContext context, Optional<Long> timeoutTick) {
        return new QueuedEvent(resource, context, timeoutTick.orElse(null));
    }
}
