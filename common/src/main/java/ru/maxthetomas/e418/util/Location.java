package ru.maxthetomas.e418.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import ru.maxthetomas.e418.E418;

public record Location(ServerLevel level, Vec3 position) {

    /**
     * Creates an Location from ServerPlayer's position.
     */
    public static Location fromPlayer(ServerPlayer player) {
        var position = player.position();
        var level = player.serverLevel();
        return new Location(level, position);
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

        return new Location(respawnLevel, respawnPosition.getCenter());
    }

    public static Location fromDimensionIdAndVec3(ResourceLocation key, Vec3 position) {
        var server = E418.getCurrentServer();
        if (server.isEmpty()) return null;
        var resourceKey = ResourceKey.create(Registries.DIMENSION, key);
        var level = server.get().getLevel(resourceKey);

        return new Location(level, position);
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
                level.dimension().location(), position.x(), position.y(), position.z());
    }
}
