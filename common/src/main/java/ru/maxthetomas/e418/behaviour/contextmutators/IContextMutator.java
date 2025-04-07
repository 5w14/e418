package ru.maxthetomas.e418.behaviour.contextmutators;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.behaviour.impl.MutateContextBehaviour;
import ru.maxthetomas.e418.event.EventContext;

/**
 * A part of {@link MutateContextBehaviour}
 * Specifies how context will be mutated.
 */
public interface IContextMutator {
    /**
     * Mutates given context.
     *
     * @param context Context to mutate
     * @return Whether the mutation succeeded.
     */
    public boolean mutate(EventContext context);

    ResourceLocation getType();
}
