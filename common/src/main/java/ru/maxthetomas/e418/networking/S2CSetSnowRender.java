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

public record S2CSetSnowRender(boolean shouldRenderSnow) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "set_should_render_snow");
    public static final Type<S2CSetSnowRender> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetSnowRender> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, S2CSetSnowRender::shouldRenderSnow,
            S2CSetSnowRender::new
    );

    public static void receive(S2CSetSnowRender packet, NetworkManager.PacketContext context) {
        E418ClientVariables.ShouldDisplaySnow = packet.shouldRenderSnow();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
