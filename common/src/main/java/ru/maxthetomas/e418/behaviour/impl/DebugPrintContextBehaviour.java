package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/// Prints event's context
public class DebugPrintContextBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "debug_print_context");
    public static final MapCodec<DebugPrintContextBehaviour> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(DebugPrintContextBehaviour::new));

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        for (Method method : context.getClass().getMethods()) {
            var name = method.getName();
            if (Stream.of("get", "is").anyMatch(name::startsWith)
                    && method.getParameterCount() == 0 && !name.equals("getClass")) {
                try {
                    var result = method.invoke(context);
                    if (result == null)
                        throw new NullPointerException();

                    context.getServer().getPlayerList().broadcastSystemMessage(
                            Component.literal(name).append(" -> ").append(
                                            Component.literal(result.toString())
                                                    .withStyle(ChatFormatting.GRAY))
                                    .withStyle(ChatFormatting.WHITE)
                            , false);
                } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
                    context.getServer().getPlayerList().broadcastSystemMessage(
                            Component.literal(name + " -> ")
                                    .append(Component.literal("null").withStyle(Style.EMPTY
                                            .applyFormat(ChatFormatting.DARK_GRAY)
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(e.toString()))))),
                            false);
                }
            }
        }
    }
}