package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.ActiveEvent;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

public class TimeoutBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "timeout");
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
        TickEvent.SERVER_PRE.register(this::processTick);
    }

    private void processTick(MinecraftServer server) {
        if (server.overworld().getGameTime() >= endTick && !isDone())
            end(event);
    }

    private void end(ActiveEvent event) {
        E418.getEventManager().stopEvent(event);
        setDone(true);
    }

    @Override
    public void dispose() {
        TickEvent.SERVER_PRE.unregister(this::processTick);
        super.dispose();
    }
}