package ru.maxthetomas.votvevents.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.condition.Conditions;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

import java.util.List;

public class OrCondition implements ICondition {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "or");
    public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Conditions.DISPATCH_CODEC.listOf().fieldOf("conditions").forGetter(OrCondition::getConditions)
    ).apply(instance, OrCondition::new));

            private final List<ICondition> conditions;

    public OrCondition(List<ICondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean check(EventContext context) {
        return conditions.stream().anyMatch(v -> v.check(context));
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    public List<ICondition> getConditions() {
        return conditions;
    }
}
