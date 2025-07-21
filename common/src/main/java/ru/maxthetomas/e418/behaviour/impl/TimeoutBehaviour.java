package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.codecs.NumberProvider;
import ru.maxthetomas.e418.codecs.NumberProviders;
import ru.maxthetomas.e418.codecs.impl.ConstantNumberProvider;
import ru.maxthetomas.e418.event.ActiveEvent;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

/// Stops event after specified amount of ticks
///
/// <li> <code>ticks</code> - Ticks to stop
public class TimeoutBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "timeout");
    public static final MapCodec<TimeoutBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("ticks").forGetter(TimeoutBehaviour::getTicks)
    ).apply(instance, TimeoutBehaviour::new));

    public static final MapCodec<TimeoutBehaviour> STATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.LONG.fieldOf("end_tick").forGetter(v -> v.endTick)
    ).apply(instance, TimeoutBehaviour::new));

    private final NumberProvider ticks;
    private long endTick = -1;

    public TimeoutBehaviour(NumberProvider ticks) {
        this.ticks = ticks;
    }

    private TimeoutBehaviour(long endTick) {
        this.endTick = endTick;
        this.ticks = new ConstantNumberProvider(endTick);
    }

    public NumberProvider getTicks() {
        return ticks;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        this.endTick = (int) (context.getSourceEvent().startTime + ticks.get(context, this).longValue());
        this.context = context;
    }

    @Override
    public void tick() {
        super.tick();
        if (isDone()) return;
        if (context.getServer().overworld().getGameTime() > this.endTick)
            end(context.getSourceEvent());
    }

    private void end(ActiveEvent event) {
        E418.getEventManager().stopEvent(event);
        setDone(true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }


    @Override
    public void restoreState(EventContext context, IBehaviourExecutor executor) {
        super.restoreState(context, executor);
    }
}