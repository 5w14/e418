package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.architecturyevents.CommandEvent;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.UUID;

/**
 * Prevents chat usage.
 * <ul>
 *   <li><code>use_context</code> – If true, only the player in the context will be muted.</li>
 * </ul>
 */
public class PreventChatUsageBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("prevent_chat_usage");
    public static final MapCodec<PreventChatUsageBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            // TODO: proposal - rename this to "global" or something, and invert behaviour
            // or have the same approach as in other behaviours, which will affect player if the context has one
            // - max
            Codec.BOOL.optionalFieldOf("use_context", false).forGetter(PreventChatUsageBehaviour::isUsingContext)
    ).apply(instance, PreventChatUsageBehaviour::new));

    public static final MapCodec<PreventChatUsageBehaviour> STATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.lenientOptionalFieldOf("player_target", null).forGetter(v -> v.playerTarget)
    ).apply(instance, PreventChatUsageBehaviour::new));

    private final ChatEvent.Received onChatMessage = this::onChatMessage;
    private final CommandEvent.MsgReceived onMsg = this::onMsg;

    private final boolean usingContext;
    private UUID playerTarget;

    public PreventChatUsageBehaviour(boolean usingContext) {
        this.usingContext = usingContext;
        ChatEvent.RECEIVED.register(onChatMessage);
        CommandEvent.MSG_RECEIVED.register(onMsg);
    }

    private PreventChatUsageBehaviour(@Nullable UUID playerTarget) {
        this.playerTarget = playerTarget;
        this.usingContext = this.playerTarget != null;
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
                playerTarget = contextTarget.getUUID();
            } else {
                setDone(true);
            }
        }
    }


    @Override
    public void dispose() {
        super.dispose();
        ChatEvent.RECEIVED.unregister(onChatMessage);
        CommandEvent.MSG_RECEIVED.unregister(onMsg);
    }

    private EventResult onChatMessage(ServerPlayer serverPlayer, Component component) {
        if (!isExecuted() || isDisposed() || isDone())
            return EventResult.pass();

        if (playerTarget == null || playerTarget.equals(serverPlayer.getUUID())) {
            return EventResult.interruptFalse();
        }

        return EventResult.pass();
    }

    private EventResult onMsg(PlayerChatMessage msg) {
        if (!isExecuted() || isDisposed() || isDone())
            return EventResult.pass();

        if (playerTarget == null || playerTarget.equals(msg.sender())) {
            return EventResult.interruptFalse();
        }

        return EventResult.pass();

    }

    public boolean isUsingContext() {
        return usingContext;
    }

    @Override
    public void restoreState(EventContext context, IBehaviourExecutor executor) {
        super.restoreState(context, executor);
        if (!isDone() && !isDisposed() && isExecuted()) {
            if (usingContext)
                playerTarget = context.getPlayerUUID();
        }
    }
}

