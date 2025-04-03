package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class ModifyHeldItemComponents extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "modify_held_item_components");

    public static final MapCodec<ModifyHeldItemComponents> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ModifyHeldItemComponents::getComponents)
    ).apply(instance, ModifyHeldItemComponents::new));


    private final DataComponentPatch components;

    public ModifyHeldItemComponents(DataComponentPatch components) {
        this.components = components;
    }

    @Override
    public void execute(EventContext context) {
        super.execute(context);
        setDone(true);

        if (context.getPlayer() != null) {
            return;
        }

        var item = context.getPlayer().getItemInHand(InteractionHand.MAIN_HAND);
        item.applyComponents(components);
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public boolean canRun(EventContext context) {
        return context.getPlayer() != null;
    }

    public DataComponentPatch getComponents() {
        return components;
    }
}
