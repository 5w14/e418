package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.util.E418Variables;

/**
 * Prevents chat usage
 */
public class PreventChatUsageBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "prevent_chat_usage");
    public static final MapCodec<PreventChatUsageBehaviour> CODEC = MapCodec.unit(PreventChatUsageBehaviour::new);

    @Override
    public ResourceLocation getTypeId() {
        return null;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        ChatEvent.RECEIVED.register(this::onChatMessage);
        E418Variables.PreventMsgUsage = true;
    }

    @Override
    public void dispose() {
        super.dispose();
        ChatEvent.RECEIVED.unregister(this::onChatMessage);
        E418Variables.PreventMsgUsage = false;
    }

    private EventResult onChatMessage(ServerPlayer serverPlayer, Component component) {
        return EventResult.interruptFalse();
    }
}

