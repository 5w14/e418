package ru.maxthetomas.e418.event.cause;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.cause.impl.*;

import java.util.HashMap;
import java.util.Map;

/// Registry for EventCauses for serialization.
public class EventCauses {
    private static final Map<ResourceLocation, MapCodec<? extends IEventCause>> CAUSES = new HashMap<>();
    public static Codec<IEventCause> DISPATCH_CODEC = ResourceLocation.CODEC
            .dispatch(IEventCause::getType, CAUSES::get);

    public static final MapCodec<? extends IEventCause> CHAT_MESSAGE = register(ChatMessageCause.TYPE, ChatMessageCause.CODEC);
    public static final MapCodec<? extends IEventCause> CONSOLE_COMMAND = register(ConsoleCommandEventCause.TYPE, ConsoleCommandEventCause.CODEC);
    public static final MapCodec<? extends IEventCause> GLOBAL_RANDOM = register(GlobalRandomEventCause.TYPE, GlobalRandomEventCause.CODEC);
    public static final MapCodec<? extends IEventCause> PLAYER_RANDOM = register(PlayerRandomEventCause.TYPE, PlayerRandomEventCause.CODEC);
    public static final MapCodec<? extends IEventCause> WAKE_UP = register(WakeUpEventCause.TYPE, WakeUpEventCause.CODEC);

    public static MapCodec<? extends IEventCause> register(ResourceLocation resourceLocation, MapCodec<? extends IEventCause> codec) {
        CAUSES.put(resourceLocation, codec);
        return codec;
    }

    public static MapCodec<IEventCause> register(String name, MapCodec<IEventCause> codec) {
        CAUSES.put(ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, name), codec);
        return codec;
    }

}
