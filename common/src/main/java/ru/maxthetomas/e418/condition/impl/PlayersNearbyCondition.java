package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.FloatRange;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when there are enough players within the radius.
 * <ul>
 *   <li><code>radius</code> - Range where it counts players in blocks/meters.</li>
 *   <li><code>player_range_min</code> - Minimum number of players required to trigger.</li>
 *   <li><code>player_range_max</code> - Maximum number of players allowed to trigger.</li>
 * </ul>
 */
public record PlayersNearbyCondition(double radius, FloatRange playerRange) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("players_nearby");
    public static final MapCodec<PlayersNearbyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", 1d).forGetter(PlayersNearbyCondition::getRadius),
            FloatRange.CODEC.codec().optionalFieldOf("player_range", new FloatRange(0, 5)).forGetter(PlayersNearbyCondition::playerRange)
    ).apply(instance, PlayersNearbyCondition::new));

    @Override
    public boolean check(EventContext context) {
        var playersNearby = 0;
        var player = context.getPlayer();

        for (Player otherPlayer : player.level().players()) {
            if (otherPlayer == player) {
                continue;
            }

            if (otherPlayer.distanceTo(player) <= radius &&
                    !otherPlayer.isSpectator() &&
                    otherPlayer.isAlive()) {
                playersNearby++;
            }
        }

        return playersNearby >= playerRange.min() && playersNearby <= playerRange.max();
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public Double getRadius() {
        return radius;
    }
}
