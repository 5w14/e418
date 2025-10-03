package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

/**
 * Stops the event.
 */
public class SpawnEntityBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("spawn_entity");
    public static final MapCodec<SpawnEntityBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("entity").forGetter(v -> v.entityToSpawn)
    ).apply(instance, SpawnEntityBehaviour::new));

    public static final MapCodec<SpawnEntityBehaviour> STATE_CODEC = CODEC;

    private final ResourceLocation entityToSpawn;
    public SpawnEntityBehaviour(ResourceLocation entityToSpawn) { 
        this.entityToSpawn = entityToSpawn;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        setDone(true);

        var location = context.getLocation();
        var entity = context.getServer().registryAccess().lookup(Registries.ENTITY_TYPE)
            .orElseThrow().getValue(this.entityToSpawn);
        entity.spawn(location.level(), location.getBlockPosition(), EntitySpawnReason.EVENT);
    }

    @Override
    public boolean canRun(EventContext context) {
        return context.getLocation() != null;
    }
}
