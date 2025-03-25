package ru.maxthetomas.votvevents.behaviour.impl;

import com.google.gson.JsonElement;
import net.minecraft.network.chat.Component;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class BroadcastChatMessageBehaviour implements IBehaviour {
    Component message;

    public BroadcastChatMessageBehaviour(JsonElement properties) {
        var message = properties.getAsJsonObject().get("message");
        this.message = Component.Serializer.fromJson(message, VotvEvents.getCurrentServer()
                .orElseThrow().registryAccess());
    }

    @Override
    public void execute(EventContext context) {
        context.server().sendSystemMessage(message);
    }
}
