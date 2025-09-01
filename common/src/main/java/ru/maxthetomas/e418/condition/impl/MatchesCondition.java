package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.ChatMessageCause;

import java.util.regex.Pattern;

/**
 * Returns true only when the regex matches the chat message.
 * <ul>
 *   <li><code>regex</code> - Regex to check.</li>
 * </ul>
 */
public class MatchesCondition implements ICondition {
    public static final MapCodec<MatchesCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("regex").forGetter(MatchesCondition::getRegex)
    ).apply(instance, MatchesCondition::new));
    public static final ResourceLocation ID = E418.resLoc("chat/matches");

    private final String regex;

    MatchesCondition(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean check(EventContext context) {
        var cause = context.getCause();
        if (cause.getClass() != ChatMessageCause.class) {
            return true;
        }
        var message = ((ChatMessageCause) cause).context();
        var messageText = message.getString().toLowerCase();

        var pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        var matcher = pattern.matcher(messageText);

        return matcher.find();
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public String getRegex() {
        return regex;
    }
}