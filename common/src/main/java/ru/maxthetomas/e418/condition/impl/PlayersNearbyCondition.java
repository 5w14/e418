package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when there are enough players within the radius.
 * <ul>
 *   <li><code>radius</code> - Range where it counts players in blocks/meters.</li>
 *   <li><code>min_players_nearby</code> - Minimum number of players required to trigger.</li>
 *   <li><code>max_players_nearby</code> - Maximum number of players allowed to trigger.</li>
 * </ul>
 */
public class PlayersNearbyCondition implements ICondition {
    public static ResourceLocation ID = E418.resLoc("players_nearby");
    public static MapCodec<PlayersNearbyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", 1d).forGetter(PlayersNearbyCondition::getRadius),
            Codec.INT.optionalFieldOf("min_players_nearby", 0).forGetter(PlayersNearbyCondition::getMinPlayerNearby),
            Codec.INT.optionalFieldOf("max_players_nearby", 0).forGetter(PlayersNearbyCondition::getMaxPlayerNearby)
    ).apply(instance, PlayersNearbyCondition::new));

    private final double radius;
    private final int minPlayersNearby;
    private final int maxPlayersNearby;

    public PlayersNearbyCondition(double radius, int minPlayersNearby, int maxPlayersNearby) {
        this.radius = radius;
        this.minPlayersNearby = minPlayersNearby;
        this.maxPlayersNearby = maxPlayersNearby;
    }

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

        return playersNearby >= minPlayersNearby && playersNearby <= maxPlayersNearby;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public Double getRadius() {
        return radius;
    }

    public int getMinPlayerNearby() {
        return minPlayersNearby;
    }

    public int getMaxPlayerNearby() {
        return minPlayersNearby;
    }
}
