package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.condition.Conditions;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

import java.util.List;
import java.util.stream.Stream;

/**
 * Returns true only when one or more conditions are true.
 * <ul>
 *   <li><code>conditions</code> - Conditions to check.</li>
 * </ul>
 */
public record OrCondition(List<Dynamic<?>> conditions) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("or");
    public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.PASSTHROUGH.listOf().fieldOf("conditions").forGetter(OrCondition::conditions)
    ).apply(instance, OrCondition::new));

    @Override
    public boolean check(EventContext context) {
        return buildConditions().anyMatch(v -> v.check(context));
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public Stream<ICondition> buildConditions() {
        return conditions.stream().map(Conditions.DISPATCH_CODEC::parse)
                .filter(DataResult::isSuccess).map(DataResult::getOrThrow);
    }
}
