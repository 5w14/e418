package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

/// Adds a new component to the item in hand
///
/// <li> <code>component</code> - Component to add
public class ModifyHeldItemComponents extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "modify_held_item_components");

    public static final MapCodec<ModifyHeldItemComponents> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ModifyHeldItemComponents::getComponents)
    ).apply(instance, ModifyHeldItemComponents::new));


    private final DataComponentPatch components;

    public ModifyHeldItemComponents(DataComponentPatch components) {
        this.components = components;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
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
