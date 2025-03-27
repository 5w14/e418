package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
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
            if (name.startsWith("get")) {
                try {
                    context.getServer().getPlayerList().broadcastSystemMessage(
                            ComponentUtils.fromMessage(Component.literal(name + " -> " + method.invoke(context).toString())), false);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    context.getServer().getPlayerList().broadcastSystemMessage(
                            ComponentUtils.fromMessage(Component.literal(name)), false);
                }
            }
        }
    }
}