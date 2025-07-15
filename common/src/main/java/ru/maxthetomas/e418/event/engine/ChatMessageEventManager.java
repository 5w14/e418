package ru.maxthetomas.e418.event.engine;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.ChatMessageCause;
import ru.maxthetomas.e418.event.registry.EventRegistries;
import ru.maxthetomas.e418.util.Location;

public class ChatMessageEventManager {
    public ChatMessageEventManager() {
        ChatEvent.RECEIVED.register(this::onReceived);
    }

    private EventResult onReceived(@Nullable ServerPlayer serverPlayer, Component component) {
        assert serverPlayer != null;

        var ctx = new EventContext(serverPlayer.getServer())
                .withPlayer(serverPlayer)
                .withLocation(Location.fromPlayer(serverPlayer))
                .withCause(new ChatMessageCause(component));

        var e = EventRegistries.getQueueableEventsWithTag("action.minecraft.chat_message", ctx).getRandomElement();
        if (e != null) {
            E418.getEventManager().queueEvent(e, ctx);
        }



        return EventResult.pass();
    }

}
