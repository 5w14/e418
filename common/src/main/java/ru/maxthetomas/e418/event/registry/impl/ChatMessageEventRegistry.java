package ru.maxthetomas.e418.event.registry.impl;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.SourceConfig;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.IEventCause;
import ru.maxthetomas.e418.event.registry.EventRegistry;

public class ChatMessageEventRegistry extends EventRegistry<SourceConfig> {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "chat_message");

    public ChatMessageEventRegistry(float defaultChance) {
        this.config = new SourceConfig(true, defaultChance);
    }

    @Override
    protected void startEvent(IEventCause cause) {
        var events = getEvents();
        var ctx = new EventContext(E418.getCurrentServer().get())
                .withCause(cause);

        for (WeightedEvent event : events) {
            if (!event.resource().canRun(ctx)) {
                continue;
            }

            E418.getEventManager().runEvent(event.resource(), ctx);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
