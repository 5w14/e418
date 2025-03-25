package ru.maxthetomas.votvevents.condition.impl;

import com.google.gson.JsonElement;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

public class AtHeightCondition implements ICondition {
    private boolean needBoth = true;
    private float above = 0;
    private float below = 0;

    public AtHeightCondition(JsonElement properties) {
        if (properties.getAsJsonObject().has("need_both")) {
            needBoth = properties.getAsJsonObject().get("need_both").getAsBoolean();
        }

        if (properties.getAsJsonObject().has("above")) {
            above = properties.getAsJsonObject().get("above").getAsFloat();
        }

        if (properties.getAsJsonObject().has("below")) {
            below = properties.getAsJsonObject().get("below").getAsFloat();
        }
    }

    @Override
    public boolean check(EventContext context) {
        var positionY = context.getPlayer().position().y;

        if (needBoth) {
            return positionY < below && positionY > above;
        } else {
            return positionY < below || positionY > above;
        }
    }
}
