package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.codecs.NumberProvider;
import ru.maxthetomas.e418.codecs.NumberProviders;
import ru.maxthetomas.e418.codecs.impl.ConstantNumberProvider;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.system.TemporalShiftSystem;
import ru.maxthetomas.e418.util.Location;

/**
 * Saves player's position and then teleports them back to it.
 * If player disconnects while behaviour is running, they are immediately teleported
 * <ul>
 *   <li><code>ticks</code> – Ticks until teleportation</li>
 * </ul>
 */
public class TemporalShiftBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("temporal_shift");
    public static final MapCodec<TemporalShiftBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("ticks").forGetter(TemporalShiftBehaviour::getTicks)
    ).apply(instance, TemporalShiftBehaviour::new));

    public static final MapCodec<TemporalShiftBehaviour> STATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Location.CODEC.fieldOf("location").forGetter(v -> v.location),
            Codec.LONG.fieldOf("end_tick").forGetter(v -> v.endTick)
    ).apply(instance, TemporalShiftBehaviour::new));

    private final NumberProvider ticks;

    private Location location;
    private long endTick = -1;

    public TemporalShiftBehaviour(NumberProvider ticks) {
        this.ticks = ticks;
    }

    public TemporalShiftBehaviour(Location location, long endTick) {
        this.endTick = endTick;
        this.ticks = new ConstantNumberProvider(endTick);
        this.location = location;
    }

    @Override
    public void tick() {
        super.tick();

        if (isDone()) {
            return;
        }

        if (context.getServer().overworld().getGameTime() >= endTick) {
            setDone(true);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (context.getPlayer() == null ||
                TemporalShiftSystem.getPlayersInShift().get(context.getPlayer().getUUID().toString()) == null) {
            return;
        }

        context.getPlayer().teleport(new TeleportTransition(
                location.level(),
                location.position(),
                Vec3.ZERO,
                context.getPlayer().getYRot(),
                context.getPlayer().getXRot(),
                TeleportTransition.DO_NOTHING));

        TemporalShiftSystem.removeShift(context.getPlayerUUID());
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    public NumberProvider getTicks() {
        return ticks;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);

        this.context = context;
        this.endTick = (int) (context.getSourceEvent().startTime + ticks.get(context, this).longValue());
        this.location = Location.fromPlayer(context.getPlayer());

        TemporalShiftSystem.addShift(context.getPlayerUUID(), location);
    }
}
