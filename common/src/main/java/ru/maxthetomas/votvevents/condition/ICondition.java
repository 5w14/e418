package ru.maxthetomas.votvevents.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.event.EventContext;

/**
 * Event condition to run or queue.
 * These conditions are generic actions that event could check.
 */
@FunctionalInterface
public interface ICondition {

    /**
     * Check if condition is met
     *
     * @return Is condition is met
     */
    boolean check(EventContext context);

    public MapCodec<? extends ICondition> getType();

    public ResourceLocation getTypeId();
}
