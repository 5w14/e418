package ru.maxthetomas.e418.behaviour.contextmutators.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.util.E418Random;
import ru.maxthetomas.e418.util.Location;

public class SelectRandomLocationAroundPlayerContextMutator implements IContextMutator {
    public static final ResourceLocation ID = E418.resLoc("select_random_location_around_player");
    public static final MapCodec<SelectRandomLocationAroundPlayerContextMutator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.optionalFieldOf("radius", 16.0f)
                            .forGetter(SelectRandomLocationAroundPlayerContextMutator::getRange),
                    ResourceLocation.CODEC.optionalFieldOf("random_sequence", E418Random.EVENT_GENERIC_RESOURCE)
                            .forGetter(SelectRandomLocationAroundPlayerContextMutator::getRandomSequence),
                    Codec.BOOL.optionalFieldOf("use_heightmap", false).forGetter(v -> v.useHeightmap),
                    Heightmap.Types.CODEC.optionalFieldOf("heightmap_type", Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)
                            .forGetter(v -> v.heightMapType)
            ).apply(instance, SelectRandomLocationAroundPlayerContextMutator::new));

    private final float range;
    private final ResourceLocation randomSequence;
    private final boolean useHeightmap;
    private final Heightmap.Types heightMapType;

    public SelectRandomLocationAroundPlayerContextMutator(float range, ResourceLocation randomSequence, boolean useHeightmap, Heightmap.Types heightMapType) {
        this.range = range;
        this.randomSequence = randomSequence;
        this.useHeightmap = useHeightmap;
        this.heightMapType = heightMapType;
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

        random = context.getServer().overworld().getRandomSequence(randomSequence);

        var position = location.position().add(
                (random.nextFloat() - 0.5f) * range * 2,
                (random.nextFloat() - 0.5f) * range * 2,
                (random.nextFloat() - 0.5f) * range * 2
        );

        if (useHeightmap) {
            position = new Vec3(
                    position.x,
                    location.level().getHeight(heightMapType, location.getBlockPosition().getX(),
                            location.getBlockPosition().getZ()),
                    position.z
            );
        }

        var newLocation = new Location(
                location.levelId(),
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
