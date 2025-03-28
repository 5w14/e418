package ru.maxthetomas.votvevents.condition;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * Event condition to run or queue.
 * These conditions are generic actions that event could check.
 */
public interface ICondition {

    /**
     * Check if condition is met.
     *
     * @param context Context of event.
     * @return Is condition is met.
     */
    boolean check(EventContext context);

    public ResourceLocation getType();
}
