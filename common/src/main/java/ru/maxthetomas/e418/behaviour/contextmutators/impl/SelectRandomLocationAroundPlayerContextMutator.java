package ru.maxthetomas.e418.behaviour.contextmutators.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.util.Location;

public class SelectRandomLocationAroundPlayerContextMutator implements IContextMutator {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "select_random_location_around_player");
    public static final MapCodec<SelectRandomLocationAroundPlayerContextMutator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.optionalFieldOf("radius", 16.0f)
                            .forGetter(SelectRandomLocationAroundPlayerContextMutator::getRange)
            ).apply(instance, SelectRandomLocationAroundPlayerContextMutator::new));

    private final float range;

    public SelectRandomLocationAroundPlayerContextMutator(float range) {
        this.range = range;
    }

    public float getRange() {
        return range;
    }

    @Override
    public boolean mutate(EventContext context) {
        var player = context.getPlayer();
        if (player == null) return false;

        var location = Location.fromPlayerSpawnLocation(player);
        var random = RandomSource.create();

        var position = location.getPosition();
        position.add(
                (random.nextFloat() - 0.5f) * range * 2,
                (random.nextFloat() - 0.5f) * range * 2,
                (random.nextFloat() - 0.5f) * range * 2
        );

        var newLocation = new Location(
                location.getLevel(),
                position
        );

        context.withLocation(newLocation);
        context.withPlayer(null);

        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
