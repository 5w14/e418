package ru.maxthetomas.votvevents.behaviour.impl;

import com.google.gson.JsonElement;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.Consumable;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class MakeConsumableBehaviour implements IBehaviour {
    private float consumeSeconds = 2;
    private boolean hasConsumeParticles = false;

    public MakeConsumableBehaviour(JsonElement properties) {
        if (properties.getAsJsonObject().has("consume_seconds")) {
            consumeSeconds = properties.getAsJsonObject().get("consume_seconds").getAsFloat();
        }

        if (properties.getAsJsonObject().has("has_consume_particles")) {
            hasConsumeParticles = properties.getAsJsonObject().get("has_consume_particles").getAsBoolean();
        }
    }

    @Override
    public void execute(EventContext context) {
        var item = context.getPlayer().getItemInHand(InteractionHand.MAIN_HAND);

        item.set(DataComponents.CONSUMABLE, Consumable.builder()
                .consumeSeconds(consumeSeconds)
                .hasConsumeParticles(hasConsumeParticles)
                .build());
    }
}
