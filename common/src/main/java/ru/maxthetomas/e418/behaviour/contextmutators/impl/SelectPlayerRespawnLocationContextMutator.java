package ru.maxthetomas.e418.behaviour.contextmutators.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.util.Location;

/**
 * Mutates context to have player's respawn location.
 */
public record SelectPlayerRespawnLocationContextMutator(boolean preventOverride) implements IContextMutator {
    public static final ResourceLocation ID = E418.resLoc("select_player_respawn_location");
    public static final MapCodec<SelectPlayerRespawnLocationContextMutator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("prevent_override", false).forGetter(SelectPlayerRespawnLocationContextMutator::preventOverride)
    ).apply(instance, SelectPlayerRespawnLocationContextMutator::new));

    /**
     * Is this mutator prevents override of already defined fields.
     *
     * @return Is mutation doesn't override non-null fields
     */
    @Override
    public boolean preventOverride() {
        return preventOverride;
    }

    @Override
    public boolean mutate(EventContext context) {
        if (preventOverride && context.getLocation() != null) {
            return true;
        }

        var player = context.getPlayer();
        if (!(player instanceof ServerPlayer sp)) return false;
        context.withLocation(Location.fromPlayerSpawnLocation(sp));

        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
