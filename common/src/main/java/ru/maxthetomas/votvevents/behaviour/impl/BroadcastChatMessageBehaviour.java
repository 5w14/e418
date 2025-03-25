package ru.maxthetomas.votvevents.behaviour.impl;

import com.google.gson.JsonElement;
import net.minecraft.network.chat.Component;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class BroadcastChatMessageBehaviour implements IBehaviour {
    JsonElement message;

    public BroadcastChatMessageBehaviour(JsonElement properties) {
        this.message = properties.getAsJsonObject().get("message");
    }

    @Override
    public void execute(EventContext context) {
        var message = Component.Serializer.fromJson(this.message, context.getServer().registryAccess());
        context.getServer().sendSystemMessage(message);
    }
}
