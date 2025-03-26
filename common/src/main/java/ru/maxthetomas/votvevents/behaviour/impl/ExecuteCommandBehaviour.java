package ru.maxthetomas.votvevents.behaviour.impl;

import com.google.gson.JsonElement;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class ExecuteCommandBehaviour implements IBehaviour {
    private boolean executeAsPlayer;
    private String command;

    public ExecuteCommandBehaviour(JsonElement properties) {
        if (properties.getAsJsonObject().has("command")) {
            command = properties.getAsJsonObject().get("command").getAsString();
        }

        if (properties.getAsJsonObject().has("execute_as_player")) {
            executeAsPlayer = properties.getAsJsonObject().get("execute_as_player").getAsBoolean();
        }
    }

    @Override
    public void execute(EventContext context) {
        if (executeAsPlayer) {
            context.getServer().getCommands().performPrefixedCommand(
                    ((ServerPlayer) context.getPlayer()).createCommandSourceStack(),
                    command);
        } else {
            context.getServer().getCommands().performPrefixedCommand(
                    context.getServer().createCommandSourceStack(),
                    command);
        }

    }
}
