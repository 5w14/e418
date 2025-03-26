package ru.maxthetomas.votvevents.behaviour.impl;

import com.google.gson.JsonElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DebugPrintContextBehaviour implements IBehaviour {

    public DebugPrintContextBehaviour(JsonElement properties) {
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