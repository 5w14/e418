package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.networking.S2CShowToast;

/**
 * Gives the player a custom achievement.
 * <ul>
 *   <li><code>title</code> - Achievement's title.</li>
 *   <li><code>description</code> - Achievement's description.</li>
 *   <li><code>stack</code> - Achievement's picture.</li>
 * </ul>
 */
public class ShowToastBehaviour extends Behaviour {
    // TODO add "toast_type" argument
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "show_toast");
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
        super.execute(context, executor);
        setDone(true);

        var player = context.getPlayer();
        if (player == null) return;

        NetworkManager.sendToPlayer(player, new S2CShowToast(
                title, description, stack
        ));
    }

    @Override
    public boolean canRun(EventContext context) {
        return context.getPlayer() != null;
    }
}
