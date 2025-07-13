package ru.maxthetomas.e418.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.event.cause.impl.ChatMessageCause;
import ru.maxthetomas.e418.event.registry.EventRegistries;

public class ChatMessageEventManager {
    public ChatMessageEventManager() {
        ChatEvent.RECEIVED.register(this::onReceived);
    }

    private EventResult onReceived(@Nullable ServerPlayer serverPlayer, Component component) {
        EventRegistries.CHAT_MESSAGE.eventTick(new ChatMessageCause(component));

        return EventResult.pass();
    }

}
