package ru.maxthetomas.e418.behaviour.contextmutators.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Mutates context to have random player.
 */
public class SelectRandomPlayerContextMutator implements IContextMutator {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "select_random_player");
    public static final MapCodec<SelectRandomPlayerContextMutator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("prevent_override", false).forGetter(SelectRandomPlayerContextMutator::isPreventOverride),
            ResourceLocation.CODEC.optionalFieldOf("random_sequence", null).forGetter(SelectRandomPlayerContextMutator::getRandomSequence)
    ).apply(instance, SelectRandomPlayerContextMutator::new));

    private final boolean preventOverride;
    private final ResourceLocation randomSequence;

    public SelectRandomPlayerContextMutator(boolean preventOverride, ResourceLocation randomSequence) {
        this.preventOverride = preventOverride;
        this.randomSequence = randomSequence;
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
        
        RandomSource random;

        if (randomSequence != null) {
            random = context.getServer().overworld().getRandomSequence(randomSequence);
        } else {
            random = context.getServer().overworld().getRandom();
        }

        ServerPlayer target = playerList.get(random.nextIntBetweenInclusive(0, playerList.size() - 1));
        context.withPlayer(target);

        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    @Nullable
    private ResourceLocation getRandomSequence() {
        return randomSequence;
    }
}
