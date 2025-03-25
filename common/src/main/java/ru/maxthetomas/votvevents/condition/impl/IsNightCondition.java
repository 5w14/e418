package ru.maxthetomas.votvevents.condition.impl;

import com.google.gson.JsonElement;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

public class IsNightCondition implements ICondition {
    public IsNightCondition(JsonElement jsonElement) {
    }

    @Override
    public boolean check(EventContext context) {
        return context.getServer().overworld().isNight();
    }
}
