package ru.maxthetomas.e418.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.FogCodecs;
import ru.maxthetomas.e418.util.E418ClientVariables;

public record S2CSetFog(boolean enabled, FogCodecs.FogConfig config) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = E418.resLoc("set_fog");
    public static final CustomPacketPayload.Type<S2CSetFog> TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetFog> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, S2CSetFog::enabled,
            FogCodecs.STREAM_CODEC, S2CSetFog::config,
            S2CSetFog::new
    );

    public static void receive(S2CSetFog packet, NetworkManager.PacketContext context) {
        if (!packet.enabled()) {
            E418ClientVariables.FogParametersOverride = null;
            return;
        }

        E418ClientVariables.FogParametersOverride = packet.config;
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
