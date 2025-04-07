package ru.maxthetomas.votvevents.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.util.VotvEventsClientVariables;

public record S2CSetSnowRender(boolean shouldRenderSnow) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "set_should_render_snow");
    public static final Type<S2CSetSnowRender> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetSnowRender> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, S2CSetSnowRender::shouldRenderSnow,
            S2CSetSnowRender::new
    );

    public final static ResourceLocation EMPTY_SHADER = ResourceLocation.withDefaultNamespace("empty");

    public static void receive(S2CSetSnowRender packet, NetworkManager.PacketContext context) {
        VotvEventsClientVariables.ShouldDisplaySnow = packet.shouldRenderSnow();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
