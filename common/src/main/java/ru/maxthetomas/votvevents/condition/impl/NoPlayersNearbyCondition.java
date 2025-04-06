package ru.maxthetomas.votvevents.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * A condition that succeeds only if it is the nighttime in the overworld.
 */
public class NoPlayersNearbyCondition implements ICondition {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "no_players_nearby");
    public static MapCodec<NoPlayersNearbyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", 1d).forGetter(NoPlayersNearbyCondition::getRadius)
    ).apply(instance, NoPlayersNearbyCondition::new));

    private final double radius;

    public NoPlayersNearbyCondition(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean check(EventContext context) {
        Player player = context.getPlayer();

        for (Player otherPlayer : player.level().players()) {
            if (otherPlayer == player) {
                continue;
            }

            return otherPlayer.distanceTo(player) > radius &&
                    !otherPlayer.isSpectator() &&
                    otherPlayer.isAlive();
        }

        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    private Double getRadius() {
        return radius;
    }
}
