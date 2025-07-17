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
                            .forGetter(SelectRandomLocationAroundPlayerContextMutator::getRange),
                    ResourceLocation.CODEC.optionalFieldOf("random_sequence", null)
                            .forGetter(SelectRandomLocationAroundPlayerContextMutator::getRandomSequence)
            ).apply(instance, SelectRandomLocationAroundPlayerContextMutator::new));

    private final float range;
    private final ResourceLocation randomSequence;

    public SelectRandomLocationAroundPlayerContextMutator(float range, ResourceLocation randomSequence) {
        this.range = range;
        this.randomSequence = randomSequence;
    }

    public float getRange() {
        return range;
    }

    private ResourceLocation getRandomSequence() {
        return randomSequence;
    }

    @Override
    public boolean mutate(EventContext context) {
        var player = context.getPlayer();
        if (player == null) return false;

        var location = Location.fromPlayer(player);

        RandomSource random;
        if (randomSequence != null) {
            random = context.getServer().overworld().getRandomSequence(randomSequence);
        } else {
            random = context.getServer().overworld().getRandom();
        }

        var position = location.position();
        position.add(
                (random.nextFloat() - 0.5f) * range * 2,
                (random.nextFloat() - 0.5f) * range * 2,
                (random.nextFloat() - 0.5f) * range * 2
        );

        var newLocation = new Location(
                location.level(),
                position
        );

        context.withLocation(newLocation);

        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
