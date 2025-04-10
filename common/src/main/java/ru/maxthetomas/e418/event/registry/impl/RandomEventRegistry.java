package ru.maxthetomas.e418.event.registry.impl;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.registries.RandomSourceConfig;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.IEventCause;
import ru.maxthetomas.e418.event.cause.impl.RandomEventCause;
import ru.maxthetomas.e418.event.registry.EventRegistry;

public class RandomEventRegistry extends EventRegistry<RandomSourceConfig> {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "random");

    public RandomEventRegistry() {
        super();
        this.config = new RandomSourceConfig(true, 20 * 60 * 30, 20 * 60 * 90);
    }

    @Override
    protected void startEvent(IEventCause cause) {
        if (cause == null)
            cause = new RandomEventCause();

        var event = this.getRandomEvent();
        E418.getEventManager().runEvent(event,
                new EventContext(E418.getCurrentServer().get())
                        .withCause(cause)
        );
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
