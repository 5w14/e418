package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.ActiveEvent;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;

public class TimeoutBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "timeout");
    public static final MapCodec<TimeoutBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("ticks").forGetter(TimeoutBehaviour::getTicks)
    ).apply(instance, TimeoutBehaviour::new));

    private final int ticks;
    private int endTick;
    private ActiveEvent event;

    public TimeoutBehaviour(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        this.endTick = (int) (context.getSourceEvent().startTime + ticks);
        this.event = context.getSourceEvent();
        TickEvent.SERVER_POST.register(this::processTick);
    }

    private void processTick(MinecraftServer server) {
        if (server.overworld().getGameTime() >= endTick && !isDone())
            end(event);
    }

    private void end(ActiveEvent event) {
        setDone(true);
        TickEvent.SERVER_POST.unregister(this::processTick);
        VotvEvents.getEventManager().stopEvent(event);
    }
}