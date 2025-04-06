package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;

public class BroadcastMessageBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "broadcast_message");
    public static final MapCodec<? extends Behaviour> CODEC = RecordCodecBuilder.<BroadcastMessageBehaviour>mapCodec(instance ->
            instance.group(
                    ComponentSerialization.CODEC.fieldOf("message").forGetter(BroadcastMessageBehaviour::getMessage),
                    MessageSource.CODEC.optionalFieldOf("source", MessageSource.CHAT).forGetter(BroadcastMessageBehaviour::getSource)
            ).apply(instance, BroadcastMessageBehaviour::new)
    );

    Component message;
    MessageSource source;

    public BroadcastMessageBehaviour(Component message, MessageSource source) {
        this.message = message;
        this.source = source;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    public Component getMessage() {
        return message;
    }

    public MessageSource getSource() {
        return source;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        var source = getSource();

        switch (source) {
            case CHAT -> {
                context.getServer().getPlayerList().broadcastSystemMessage(message, false);
            }

            case ACTIONBAR -> {
                context.getServer().getPlayerList().broadcastSystemMessage(message, true);
            }

            case TITLE -> {
                var s2cPacket = new ClientboundSetTitleTextPacket(message);
                context.getServer().getPlayerList().broadcastAll(s2cPacket);
            }
        }
    }

    public enum MessageSource implements StringRepresentable {
        CHAT("chat"),
        ACTIONBAR("actionbar"),
        TITLE("title");

        public static Codec<MessageSource> CODEC =
                StringRepresentable.fromEnum(MessageSource::values);

        private final String id;

        MessageSource(String id) {
            this.id = id;
        }

        @Override
        public @NotNull String getSerializedName() {
            return id;
        }
    }
}
