package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.architecturyevents.CommandEvent;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * Prevents chat usage.
 * <ul>
 *   <li><code>use_context</code> – If true, only the player in the context will be muted.</li>
 * </ul>
 */
public class PreventChatUsageBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("prevent_chat_usage");
    public static final MapCodec<PreventChatUsageBehaviour> CODEC = MapCodec.unit(PreventChatUsageBehaviour::new);

    public static final MapCodec<PreventChatUsageBehaviour> STATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.lenientOptionalFieldOf("player_target").forGetter(v -> Optional.ofNullable(v.playerTarget))
    ).apply(instance, PreventChatUsageBehaviour::new));

    private final ChatEvent.Received onChatMessage = this::onChatMessage;
    private final CommandEvent.MsgReceived onMsg = this::onMsg;

    private UUID playerTarget;

    public PreventChatUsageBehaviour() {
        registerEvents();
    }

    private PreventChatUsageBehaviour(Optional<UUID> playerTarget) {
        this.playerTarget = playerTarget.orElse(null);
        registerEvents();
    }

    void registerEvents() {
        ChatEvent.RECEIVED.register(onChatMessage);
        CommandEvent.MSG_RECEIVED.register(onMsg);
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);

        if (context.hasPlayer()) {
            playerTarget = context.getPlayerUUID();
        } else playerTarget = null;
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

    @Override
    public void restoreState(EventContext context, IBehaviourExecutor executor) {
        super.restoreState(context, executor);
        if (!isDone() && !isDisposed() && isExecuted()) {
            if (context.hasPlayer())
                playerTarget = context.getPlayerUUID();
        }
    }
}

