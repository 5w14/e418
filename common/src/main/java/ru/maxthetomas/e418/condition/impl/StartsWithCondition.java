package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.ChatMessageCause;

/// Returns true only when chat messages first word was same as prefix
///
/// <li> <code>prefix</code> - Word to match
public class StartsWithCondition implements ICondition {
    public static final MapCodec<StartsWithCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("prefix").forGetter(StartsWithCondition::getPrefix)
    ).apply(instance, StartsWithCondition::new));
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "chat/starts_with");

    private final String prefix;

    StartsWithCondition(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean check(EventContext context) {
        var cause = context.getCause();
        if (cause.getClass() != ChatMessageCause.class) {
            return true;
        }
        var message = ((ChatMessageCause) cause).context;
        var messageText = message.getString().toLowerCase();

        return messageText.startsWith(prefix);
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public String getPrefix() {
        return prefix;
    }
}