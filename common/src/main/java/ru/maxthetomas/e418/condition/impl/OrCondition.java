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

public class OrCondition implements ICondition {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "or");
    public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.PASSTHROUGH.listOf().fieldOf("conditions").forGetter(OrCondition::getConditions)
    ).apply(instance, OrCondition::new));

    private final List<Dynamic<?>> conditions;

    public OrCondition(List<Dynamic<?>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean check(EventContext context) {
        return buildConditions().anyMatch(v -> v.check(context));
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public List<Dynamic<?>> getConditions() {
        return conditions;
    }

    public Stream<ICondition> buildConditions() {
        return conditions.stream().map((d) -> Conditions.DISPATCH_CODEC.parse(d))
                .filter(DataResult::isSuccess).map(DataResult::getOrThrow);
    }
}
