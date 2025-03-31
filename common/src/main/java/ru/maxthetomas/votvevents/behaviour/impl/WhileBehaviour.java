package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.behaviour.PreActiveBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

import java.util.List;

public class WhileBehaviour implements IBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "while");
    public static final MapCodec<WhileBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter(WhileBehaviour::getBehaviours),
            Codec.INT.fieldOf("interval").forGetter(WhileBehaviour::getInterval),
            Codec.INT.fieldOf("lifetime").forGetter(WhileBehaviour::getLifetime)
    ).apply(instance, WhileBehaviour::new));

    private final List<PreActiveBehaviour> behaviours;
    private final int interval;
    private final int lifetime;

    private int ticks = 0;
    private boolean isDone = false;

    private EventContext context;
    private final TickEvent.Server tickListener = this::tick;

    public WhileBehaviour(List<PreActiveBehaviour> behaviours, int interval, int lifetime) {
        this.behaviours = behaviours;
        this.interval = interval;
        this.lifetime = lifetime;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context) {
        TickEvent.SERVER_PRE.register(tickListener);
        this.context = context;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void dispose() {
        TickEvent.SERVER_PRE.unregister(tickListener);
    }

    private void tick(MinecraftServer minecraftServer) {
        ticks++;

        if (ticks % interval == 0) {
            behaviours.forEach((preActiveBehaviour) -> {
                IBehaviour behaviour = preActiveBehaviour.create();
                behaviour.execute(context);
            });
        }

        if (ticks >= lifetime) {
            isDone = true;
            context.getSourceEvent().updateState();
        }
    }

    public List<PreActiveBehaviour> getBehaviours() {
        return behaviours;
    }

    public int getInterval() {
        return interval;
    }

    public int getLifetime() {
        return lifetime;
    }
}
