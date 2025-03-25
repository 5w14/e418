package ru.maxthetomas.votvevents.behaviour.impl;

import com.google.gson.JsonElement;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

public class TeleportPlayerBehaviour implements IBehaviour {
    private boolean relative = true;
    private float x = 0;
    private float y = 0;
    private float z = 0;

    public TeleportPlayerBehaviour(JsonElement properties) {
        if (properties.getAsJsonObject().has("relative")) {
            relative = properties.getAsJsonObject().get("relative").getAsBoolean();
        }

        if (properties.getAsJsonObject().has("x")) {
            x = properties.getAsJsonObject().get("x").getAsFloat();
        }

        if (properties.getAsJsonObject().has("y")) {
            y = properties.getAsJsonObject().get("y").getAsFloat();
        }

        if (properties.getAsJsonObject().has("z")) {
            z = properties.getAsJsonObject().get("z").getAsFloat();
        }
    }

    @Override
    public void execute(EventContext context) {
        if (relative) {
            context.getPlayer().teleportRelative(x, y, z);
        } else {
            context.getPlayer().teleportTo(x, y, z);
        }
    }
}
