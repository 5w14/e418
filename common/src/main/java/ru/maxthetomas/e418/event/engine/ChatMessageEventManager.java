package ru.maxthetomas.e418.event.engine;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.ChatMessageCause;
import ru.maxthetomas.e418.event.registry.EventRegistries;
import ru.maxthetomas.e418.util.E418Random;
import ru.maxthetomas.e418.util.Location;

public class ChatMessageEventManager {

    public ChatMessageEventManager() {
        ChatEvent.RECEIVED.register((serverPlayer, component) -> serverPlayer != null ? onReceived(serverPlayer, component) : null);
    }

    private EventResult onReceived(ServerPlayer serverPlayer, Component component) {
        // TODO: Should have a limit so not every message will try to trigger an event. Can't be just random chance.

        var ctx = new EventContext(serverPlayer.getServer())
                .withPlayer(serverPlayer)
                .withLocation(Location.fromPlayer(serverPlayer))
                .withCause(new ChatMessageCause(component));

        var e = EventRegistries.getQueueableEventsWithTag("action.minecraft.chat_message", ctx, Config.baseIntrusiveness.get()).getRandomElement(E418Random.EVENT_ENGINE_WAKE_UP);

        if (e != null) {
            E418.getEventManager().queueEvent(e, ctx);
        }

        return EventResult.pass();
    }

    public void reset() {
        // nothing to reset for now
    }
}
