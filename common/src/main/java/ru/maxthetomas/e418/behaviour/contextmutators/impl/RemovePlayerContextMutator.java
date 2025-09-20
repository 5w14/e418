package ru.maxthetomas.e418.behaviour.contextmutators.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.contextmutators.IContextMutator;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Mutates context to have random player.
 */
public class RemovePlayerContextMutator implements IContextMutator {
    public static final ResourceLocation ID = E418.resLoc("remove_player");
    public static final MapCodec<RemovePlayerContextMutator> CODEC = MapCodec.unit(RemovePlayerContextMutator::new);

    public RemovePlayerContextMutator() {
    }

    @Override
    public boolean mutate(EventContext context) {
        context.withoutPlayer();
        return true;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
