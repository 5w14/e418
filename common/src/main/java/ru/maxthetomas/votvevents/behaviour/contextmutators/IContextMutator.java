package ru.maxthetomas.votvevents.behaviour.contextmutators;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.behaviour.impl.MutateContextBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

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
