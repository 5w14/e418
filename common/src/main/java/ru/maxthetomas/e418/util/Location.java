package ru.maxthetomas.e418.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import ru.maxthetomas.e418.E418;

public record Location(ResourceLocation levelId, Vec3 position) {
    public static final MapCodec<Location> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(v -> v.levelId),
            Vec3.CODEC.fieldOf("position").forGetter(v -> v.position)
    ).apply(instance, Location::fromDimensionIdAndVec3));

    public ServerLevel level() {
        return E418.getCurrentServer().orElseThrow().getLevel(ResourceKey
                .create(Registries.DIMENSION, this.levelId()));
    }

    /**
     * Creates a Location from ServerPlayer's position.
     */
    public static Location fromPlayer(ServerPlayer player) {
        var position = player.position();
        return new Location(player.level().dimension().location(), position);
    }

    /**
     * Creates a Location from ServerPlayer's spawn position.
     */
    public static Location fromPlayerSpawnLocation(ServerPlayer player) {
        if (player.getServer() == null) return null;

        var respawnPosition = player.getRespawnPosition();
        var respawnLevel = player.getServer().getLevel(player.getRespawnDimension());

        if (respawnLevel == null || respawnPosition == null)
            return null;

        return new Location(player.getRespawnDimension().location(), respawnPosition.getCenter());
    }

    public static Location fromDimensionIdAndVec3(ResourceLocation key, Vec3 position) {
        var server = E418.getCurrentServer();
        if (server.isEmpty()) return null;
        return new Location(key, position);
    }

    public BlockPos getBlockPosition() {
        return new BlockPos(new Vec3i(
                (int) Math.floor(position.x()),
                (int) Math.floor(position.y()),
                (int) Math.floor(position.z())
        ));
    }

    @Override
    public String toString() {
        return String.format("Location[level=%s, x=%.2f, y=%.2f, z=%.2f]",
                levelId, position.x(), position.y(), position.z());
    }
}
