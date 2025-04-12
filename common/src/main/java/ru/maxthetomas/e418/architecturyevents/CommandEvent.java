package ru.maxthetomas.e418.architecturyevents;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.network.chat.PlayerChatMessage;

public interface CommandEvent {
    Event<CommandEvent.MsgReceived> MSG_RECEIVED = EventFactory.createEventResult();

    @FunctionalInterface
    interface MsgReceived {
        EventResult msgReceived(PlayerChatMessage msg);
    }
}
