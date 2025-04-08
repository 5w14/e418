package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.codecs.NumberProvider;
import ru.maxthetomas.e418.codecs.NumberProviders;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

public class TeleportPlayerBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "teleport_player");

    public static final MapCodec<TeleportPlayerBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("x").forGetter(TeleportPlayerBehaviour::getX),
            NumberProviders.CODEC.fieldOf("y").forGetter(TeleportPlayerBehaviour::getY),
            NumberProviders.CODEC.fieldOf("z").forGetter(TeleportPlayerBehaviour::getZ),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("level", null).forGetter(TeleportPlayerBehaviour::getLevel),
            Codec.BOOL.optionalFieldOf("relative", true).forGetter(TeleportPlayerBehaviour::isRelative)
    ).apply(instance, TeleportPlayerBehaviour::new));
    private final ResourceKey<Level> level;
    private final boolean relative;
    private final NumberProvider x;
    private final NumberProvider y;
    private final NumberProvider z;

    public TeleportPlayerBehaviour(NumberProvider x, NumberProvider y, NumberProvider z,
                                   ResourceKey<Level> level, boolean relative) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.level = level;
        this.relative = relative;
    }


    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    public NumberProvider getX() {
        return x;
    }

    public NumberProvider getY() {
        return y;
    }

    public NumberProvider getZ() {
        return z;
    }

    public ResourceKey<Level> getLevel() {
        return level;
    }

    public boolean isRelative() {
        return relative;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        Player player = context.getPlayer();

        var newX = this.x.get(context, this).floatValue();
        var newY = this.y.get(context, this).floatValue();
        var newZ = this.z.get(context, this).floatValue();

        if (player == null) {
            return;
        }

        if (relative) {
            newX += (float) player.getX();
            newY += (float) player.getY();
            newZ += (float) player.getZ();
        }

        if (level != null) {
            player.teleport(new TeleportTransition(
                    context.getServer().getLevel(level),
                    new Vec3(newX, newY, newZ),
                    Vec3.ZERO,
                    0,
                    0,
                    TeleportTransition.DO_NOTHING));
            return;
        }

        player.teleportTo(newX, newY, newY);
    }

    @Override
    public boolean canRun(EventContext context) {
        return context.getPlayer() != null;
    }
}
