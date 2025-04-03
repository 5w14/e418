package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class TeleportPlayerBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "teleport_player");

    public static final MapCodec<TeleportPlayerBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(TeleportPlayerBehaviour::getX),
            Codec.FLOAT.fieldOf("y").forGetter(TeleportPlayerBehaviour::getY),
            Codec.FLOAT.fieldOf("z").forGetter(TeleportPlayerBehaviour::getZ),
            Codec.BOOL.optionalFieldOf("relative", true).forGetter(TeleportPlayerBehaviour::isRelative)
    ).apply(instance, TeleportPlayerBehaviour::new));

    private final float x;
    private final float y;
    private final float z;
    private final boolean relative;

    public TeleportPlayerBehaviour(float x, float y, float z, boolean relative) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public boolean isRelative() {
        return relative;
    }

    @Override
    public void execute(EventContext context) {
        super.execute(context);
        setDone(true);

        if (context.getPlayer() == null) {
            return;
        }

        if (relative) {
            context.getPlayer().teleportRelative(x, y, z);
        } else {
            context.getPlayer().teleportTo(x, y, z);
        }
    }

    @Override
    public boolean canRun(EventContext context) {
        return context.getPlayer() != null;
    }
}
