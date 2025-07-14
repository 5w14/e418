package ru.maxthetomas.e418.event.cause.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.cause.IEventCause;

public class RandomEventCause implements IEventCause {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "random_event");
    public static final MapCodec<RandomEventCause> CODEC = MapCodec.unit(new RandomEventCause());

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }
}
