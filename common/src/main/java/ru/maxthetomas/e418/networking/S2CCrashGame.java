package ru.maxthetomas.e418.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.util.exceptions.LogicalMistakeError;

public record S2CCrashGame() implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = E418.resLoc("crash_videogame");
    public static final Type<S2CCrashGame> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CCrashGame> STREAM_CODEC = StreamCodec.unit(new S2CCrashGame());

    public static void receive(S2CCrashGame packet, NetworkManager.PacketContext context) {
        LogicalMistakeError.youJustLostTheGame();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
