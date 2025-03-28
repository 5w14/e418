package ru.maxthetomas.votvevents.behaviour.impl.context_mutators.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.impl.context_mutators.IContextMutator;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * Mutates context to have random player.
 */
public class SelectRandomPlayerContextMutator implements IContextMutator {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "select_random_player");
    public static final MapCodec<SelectRandomPlayerContextMutator> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(SelectRandomPlayerContextMutator::new));

    @Override
    public boolean mutate(EventContext context) {
        var playerList = context.getServer().getPlayerList().getPlayers();

        if (playerList.isEmpty())
            return false;

        var random = context.getServer().overworld().getRandom();

        Player target = playerList.get(random.nextIntBetweenInclusive(0, playerList.size() - 1));

        context.withPlayer(target);

        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
