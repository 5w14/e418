package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.architecturyevents.CommandEvent;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

/**
 * Prevents chat usage.
 * <ul>
 *   <li><code>use_context</code> – If true, only the player in the context will be muted.</li>
 * </ul>
 */
public class PreventChatUsageBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "prevent_chat_usage");
    public static final MapCodec<PreventChatUsageBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("use_context", false).forGetter(PreventChatUsageBehaviour::isUsingContext)
    ).apply(instance, PreventChatUsageBehaviour::new));

    private final ChatEvent.Received onChatMessage = this::onChatMessage;
    private final CommandEvent.MsgReceived onMsg = this::onMsg;

    private boolean usingContext = false;
    private ServerPlayer target;

    public PreventChatUsageBehaviour(boolean usingContext) {
        this.usingContext = usingContext;
    }

    @Override
    public ResourceLocation getTypeId() {
        return null;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);


        if (usingContext) {
            var contextTarget = context.getPlayer();
            if (contextTarget != null) {
                target = contextTarget;
            } else {
                setDone(true);
                return;
            }
        }

        ChatEvent.RECEIVED.register(onChatMessage);
        CommandEvent.MSG_RECEIVED.register(onMsg);
    }


    @Override
    public void dispose() {
        super.dispose();
        ChatEvent.RECEIVED.unregister(onChatMessage);
        CommandEvent.MSG_RECEIVED.unregister(onMsg);
    }

    private EventResult onChatMessage(ServerPlayer serverPlayer, Component component) {
        if (usingContext) {
            if (serverPlayer == target) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        } else {
            return EventResult.interruptFalse();
        }
    }

    private EventResult onMsg(PlayerChatMessage msg) {
        if (usingContext) {
            if (msg.sender().equals(target.getUUID())) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        } else {
            return EventResult.interruptFalse();
        }
    }

    public boolean isUsingContext() {
        return usingContext;
    }
}

