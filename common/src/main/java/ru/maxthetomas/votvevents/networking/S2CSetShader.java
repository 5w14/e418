package ru.maxthetomas.votvevents.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.mixin.accessors.GameRendererAccessor;

public record S2CSetShader(ResourceLocation shader) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "set_shader");
    public static final Type<S2CSetShader> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetShader> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, S2CSetShader::shader,
            S2CSetShader::new
    );

    private final static ResourceLocation EMPTY_ID = ResourceLocation.withDefaultNamespace("empty");

    public static void receive(S2CSetShader packet, NetworkManager.PacketContext context) {
        var renderer = Minecraft.getInstance().gameRenderer;
        if (packet.shader.equals(EMPTY_ID)) {
            renderer.clearPostEffect();
        } else {
            ((GameRendererAccessor) renderer).callSetPostEffect(packet.shader);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
