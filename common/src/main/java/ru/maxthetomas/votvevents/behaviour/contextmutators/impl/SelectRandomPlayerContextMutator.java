package ru.maxthetomas.votvevents.behaviour.contextmutators.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * Mutates context to have random player.
 */
public class SelectRandomPlayerContextMutator implements IContextMutator {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "select_random_player");
    public static final MapCodec<SelectRandomPlayerContextMutator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("prevent_override", false).forGetter(SelectRandomPlayerContextMutator::isPreventOverride)
    ).apply(instance, SelectRandomPlayerContextMutator::new));

    private final boolean preventOverride;

    public SelectRandomPlayerContextMutator(boolean preventOverride) {
        this.preventOverride = preventOverride;
    }

    /**
     * Is this mutator prevents override of already defined fields.
     *
     * @return Is mutation doesn't override non-null fields
     */
    public boolean isPreventOverride() {
        return preventOverride;
    }

    @Override
    public boolean mutate(EventContext context) {
        if (preventOverride && context.getPlayer() != null) {
            return true;
        }

        var playerList = context.getServer().getPlayerList().getPlayers();

        if (playerList.isEmpty())
            return false;

        var random = context.getServer().overworld().getRandom();

        ServerPlayer target = playerList.get(random.nextIntBetweenInclusive(0, playerList.size() - 1));
        context.withPlayer(target);

        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
