package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class BroadcastChatMessageBehaviour implements IBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "broadcast_chat_message");
    public static final MapCodec<? extends IBehaviour> CODEC = RecordCodecBuilder.<BroadcastChatMessageBehaviour>mapCodec(instance ->
            instance.group(
                    ComponentSerialization.CODEC.fieldOf("message").forGetter(BroadcastChatMessageBehaviour::getMessage)
            ).apply(instance, BroadcastChatMessageBehaviour::new)
    );


    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    Component message;

    public BroadcastChatMessageBehaviour(Component message) {
        this.message = message;
    }

    public Component getMessage() {
        return message;
    }

    @Override
    public void execute(EventContext context) {
        context.getServer().getPlayerList().broadcastSystemMessage(message, false);
    }
}
