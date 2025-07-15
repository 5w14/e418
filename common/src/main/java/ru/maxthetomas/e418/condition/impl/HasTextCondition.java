package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.ChatMessageCause;

public class HasTextCondition implements ICondition {
    public static final MapCodec<HasTextCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("text").forGetter(HasTextCondition::getText)
    ).apply(instance, HasTextCondition::new));
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "has_text");

    private final String text;

    HasTextCondition(String text) {
        this.text = text;
    }

    @Override
    public boolean check(EventContext context) {
        var cause = context.getCause();
        if (cause.getClass() != ChatMessageCause.class) {
            return true;
        }
        var message = ((ChatMessageCause) cause).context;
        var messageText = message.getString().toLowerCase();

        return messageText.contains(text.toLowerCase());
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public String getText() {
        return text;
    }
}