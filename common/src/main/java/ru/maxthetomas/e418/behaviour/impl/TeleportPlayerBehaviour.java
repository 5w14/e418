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
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

public class TeleportPlayerBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "teleport_player");

    public static final MapCodec<TeleportPlayerBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(TeleportPlayerBehaviour::getX),
            Codec.FLOAT.fieldOf("y").forGetter(TeleportPlayerBehaviour::getY),
            Codec.FLOAT.fieldOf("z").forGetter(TeleportPlayerBehaviour::getZ),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("level", null).forGetter(TeleportPlayerBehaviour::getLevel),
            Codec.BOOL.optionalFieldOf("relative", true).forGetter(TeleportPlayerBehaviour::isRelative)
    ).apply(instance, TeleportPlayerBehaviour::new));
    private final ResourceKey<Level> level;
    private final boolean relative;
    private float x;
    private float y;
    private float z;

    public TeleportPlayerBehaviour(float x, float y, float z, ResourceKey<Level> level, boolean relative) {
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
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

        if (player == null) {
            return;
        }

        if (relative) {
            x += (float) player.getX();
            y += (float) player.getY();
            z += (float) player.getZ();
        }

        if (level != null) {
            player.teleport(new TeleportTransition(
                    context.getServer().getLevel(level),
                    new Vec3(x, y, z),
                    Vec3.ZERO,
                    0,
                    0,
                    TeleportTransition.DO_NOTHING));
            return;
        }

        player.teleportTo(x, y, z);
    }

    @Override
    public boolean canRun(EventContext context) {
        return context.getPlayer() != null;
    }
}
