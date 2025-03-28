package ru.maxthetomas.votvevents.behaviour.contextmutators.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.util.Location;

/**
 * Mutates context to have random player.
 */
public class SelectPlayerLocationContextMutator implements IContextMutator {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "select_player_location");
    public static final MapCodec<SelectPlayerLocationContextMutator> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(SelectPlayerLocationContextMutator::new));

    @Override
    public boolean mutate(EventContext context) {
        var player = context.getPlayer();
        if (!(player instanceof ServerPlayer sp)) return false;
        context.withLocation(Location.fromPlayer(sp));

        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
