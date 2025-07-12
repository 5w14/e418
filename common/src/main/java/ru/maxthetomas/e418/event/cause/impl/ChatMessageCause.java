package ru.maxthetomas.e418.event.cause.impl;

import net.minecraft.network.chat.Component;
import ru.maxthetomas.e418.event.cause.IEventCause;

public class ChatMessageCause implements IEventCause {
    public final Component context;

    public ChatMessageCause(Component context) {
        this.context = context;
    }
}
