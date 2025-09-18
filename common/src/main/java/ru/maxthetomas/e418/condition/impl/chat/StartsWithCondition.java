package ru.maxthetomas.e418.condition.impl.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.ChatMessageCause;

/**
 * Returns true only when the chat message's first word matches the prefix.
 * <ul>
 *   <li><code>prefix</code> - Word to match.</li>
 * </ul>
 */
public record StartsWithCondition(String prefix) implements ICondition {
    public static final MapCodec<StartsWithCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("prefix").forGetter(StartsWithCondition::prefix)
    ).apply(instance, StartsWithCondition::new));
    public static final ResourceLocation ID = E418.resLoc("chat/starts_with");

    @Override
    public boolean check(EventContext context) {
        var cause = context.getCause();
        if (cause.getClass() != ChatMessageCause.class) {
            return true;
        }
        var message = ((ChatMessageCause) cause).context();
        var messageText = message.getString().toLowerCase();

        return messageText.startsWith(prefix);
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}