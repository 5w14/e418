package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;
import ru.maxthetomas.votvevents.networking.S2CShowToast;

public class ShowToastBehaviour extends Behaviour {
    // TODO add "toast_type" argument
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "show_toast");
    public static final MapCodec<ShowToastBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("title").forGetter(ShowToastBehaviour::getTitle),
            ComponentSerialization.CODEC.optionalFieldOf("description", Component.empty()).forGetter(ShowToastBehaviour::getDescription),
            ItemStack.CODEC.optionalFieldOf("stack", Items.AIR.getDefaultInstance()).forGetter(ShowToastBehaviour::getStack)
    ).apply(instance, ShowToastBehaviour::new));

    private final Component title;
    private final Component description;
    private final ItemStack stack;

    public ShowToastBehaviour(Component title, Component description, ItemStack stack) {
        this.title = title;
        this.description = description;
        this.stack = stack;
    }

    public Component getTitle() {
        return title;
    }

    public Component getDescription() {
        return description;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        var player = context.getPlayer();
        if (player == null) return;

        NetworkManager.sendToPlayer((ServerPlayer) player, new S2CShowToast(
                title, description, stack
        ));

        super.execute(context, executor);
    }
}
