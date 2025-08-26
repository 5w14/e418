package ru.maxthetomas.e418.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.util.E418ClientVariables;

public record S2CSetSun(ResourceLocation sunResource) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = E418.resLoc("set_sun");
    public static final Type<S2CSetSun> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetSun> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, S2CSetSun::sunResource,
            S2CSetSun::new
    );

    public static void receive(S2CSetSun packet, NetworkManager.PacketContext context) {
        E418ClientVariables.SunResource = packet.sunResource;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
