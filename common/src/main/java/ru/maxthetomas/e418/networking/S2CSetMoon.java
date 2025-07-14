package ru.maxthetomas.e418.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.util.E418ClientVariables;

public record S2CSetMoon(ResourceLocation MoonResource) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "set_moon");
    public static final Type<S2CSetMoon> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetMoon> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, S2CSetMoon::MoonResource,
            S2CSetMoon::new
    );

    public static void receive(S2CSetMoon packet, NetworkManager.PacketContext context) {
        E418ClientVariables.MoonResource = packet.MoonResource;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
