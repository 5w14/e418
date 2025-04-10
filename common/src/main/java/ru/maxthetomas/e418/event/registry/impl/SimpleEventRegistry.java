package ru.maxthetomas.e418.event.registry.impl;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.SourceConfig;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.IEventCause;
import ru.maxthetomas.e418.event.registry.EventRegistry;

public class SimpleEventRegistry extends EventRegistry<SourceConfig> {
    protected ResourceLocation id;

    public SimpleEventRegistry(ResourceLocation id, float defaultChance) {
        this.id = id;
        this.config = new SourceConfig(true, defaultChance);
    }

    @Override
    protected void startEvent(IEventCause cause) {
        var event = getRandomEvent();
        var ctx = new EventContext(E418.getCurrentServer().get())
                .withCause(cause);
        E418.getEventManager().runEvent(event, ctx);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
