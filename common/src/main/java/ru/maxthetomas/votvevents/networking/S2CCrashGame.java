package ru.maxthetomas.votvevents.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.util.exceptions.IntegerPointerException;

public record S2CCrashGame() implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "crash_videogame");
    public static final Type<S2CCrashGame> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CCrashGame> STREAM_CODEC = StreamCodec.unit(new S2CCrashGame());

    public final static ResourceLocation EMPTY_SHADER = ResourceLocation.withDefaultNamespace("empty");

    public static void receive(S2CCrashGame packet, NetworkManager.PacketContext context) {
        IntegerPointerException.youJustLostTheGame();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
