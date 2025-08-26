package ru.maxthetomas.e418.event.cause.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.cause.IEventCause;

public class ChatMessageCause implements IEventCause {
    public static final ResourceLocation TYPE = E418.resLoc("chat_message");
    public static final MapCodec<ChatMessageCause> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ComponentSerialization.CODEC.fieldOf("context").forGetter(v -> v.context)
            ).apply(instance, ChatMessageCause::new));

    public final Component context;

    public ChatMessageCause(Component context) {
        this.context = context;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }
}
