package ru.maxthetomas.votvevents.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.advancements.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.votvevents.VotvEvents;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public record S2CShowToast(Component title, Component description, ItemStack stack) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "show_toast");
    public static final Type<S2CShowToast> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CShowToast> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, S2CShowToast::title,
            ComponentSerialization.STREAM_CODEC, S2CShowToast::description,
            ItemStack.OPTIONAL_STREAM_CODEC, S2CShowToast::stack,
            S2CShowToast::new
    );

    public static void receive(S2CShowToast packet, NetworkManager.PacketContext context) {
        var manager = Minecraft.getInstance().getToastManager();
        var stack = packet.stack;
        if (stack.is(Items.AIR)) {
            var toast = new SystemToast(new SystemToast.SystemToastId(5000), packet.title, packet.description);
            manager.addToast(toast);
        } else {
            var display = new DisplayInfo(
                    stack,
                    packet.title,
                    packet.description,
                    Optional.empty(),
                    AdvancementType.TASK,
                    true,
                    true,
                    true
            );

            var toast = new AdvancementToast(
                    new AdvancementHolder(ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "temp_advancement"),
                            new Advancement(
                                    Optional.empty(),
                                    Optional.of(display),
                                    new AdvancementRewards(0, Collections.emptyList(), Collections.emptyList(), Optional.empty()),
                                    Map.of(),
                                    AdvancementRequirements.EMPTY,
                                    false
                            )));
            manager.addToast(toast);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
