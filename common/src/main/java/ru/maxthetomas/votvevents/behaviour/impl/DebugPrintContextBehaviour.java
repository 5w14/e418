package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DebugPrintContextBehaviour implements IBehaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "debug_print_context");
    public static final MapCodec<DebugPrintContextBehaviour> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(DebugPrintContextBehaviour::new));

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context) {
        for (Method method : context.getClass().getMethods()) {
            var name = method.getName();
            if (name.startsWith("get") && method.getParameterCount() == 0 && !name.equals("getClass")) {
                try {
                    context.getServer().getPlayerList().broadcastSystemMessage(
                            Component.literal(name).append(" -> ").append(
                                            Component.literal(method.invoke(context).toString())
                                                    .withStyle(ChatFormatting.GRAY))
                                    .withStyle(ChatFormatting.WHITE)
                            , false);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    context.getServer().getPlayerList().broadcastSystemMessage(
                            Component.literal(name).withStyle(ChatFormatting.DARK_GRAY), false);
                }
            }
        }
    }
}