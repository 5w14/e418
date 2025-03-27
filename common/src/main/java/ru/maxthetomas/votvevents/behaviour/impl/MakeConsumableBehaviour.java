package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.Consumable;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class MakeConsumableBehaviour implements IBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "make_consumable");

    public static final MapCodec<MakeConsumableBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("consume_seconds", 2f).forGetter(MakeConsumableBehaviour::getConsumeSeconds),
            Codec.BOOL.optionalFieldOf("has_consume_particles", true).forGetter(MakeConsumableBehaviour::hasConsumeParticles)
    ).apply(instance, MakeConsumableBehaviour::new));


    private final float consumeSeconds;
    private final boolean hasConsumeParticles;

    public MakeConsumableBehaviour(float consumeSeconds, boolean hasConsumeParticles) {
        this.consumeSeconds = consumeSeconds;
        this.hasConsumeParticles = hasConsumeParticles;
    }

    @Override
    public void execute(EventContext context) {
        var item = context.getPlayer().getItemInHand(InteractionHand.MAIN_HAND);

        item.set(DataComponents.CONSUMABLE, Consumable.builder()
                .consumeSeconds(consumeSeconds)
                .hasConsumeParticles(hasConsumeParticles)
                .build());
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    public float getConsumeSeconds() {
        return consumeSeconds;
    }

    public boolean hasConsumeParticles() {
        return hasConsumeParticles;
    }
}
