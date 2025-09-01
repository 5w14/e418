package ru.maxthetomas.e418.condition;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.codecs.NumberRequester;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Event condition to run or queue.
 * These conditions are generic actions that event could check.
 */
public interface ICondition extends NumberRequester {

    /**
     * Check if condition is met.
     *
     * @param context Context of event.
     * @return Is condition is met.
     */
    boolean check(EventContext context);

    ResourceLocation getType();
}
