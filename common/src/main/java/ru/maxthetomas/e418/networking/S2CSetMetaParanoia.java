package ru.maxthetomas.e418.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.util.E418ClientVariables;

public record S2CSetMetaParanoia(boolean shouldHaveMetaParanoia) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = E418.resLoc("set_meta_paranoia");
    public static final Type<S2CSetMetaParanoia> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetMetaParanoia> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, S2CSetMetaParanoia::shouldHaveMetaParanoia,
            S2CSetMetaParanoia::new
    );

    public static void receive(S2CSetMetaParanoia packet, NetworkManager.PacketContext context) {
        E418ClientVariables.ShouldHaveMetaParanoia = packet.shouldHaveMetaParanoia();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
