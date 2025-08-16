package ru.maxthetomas.e418.event.cause.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.cause.IEventCause;

public class GlobalRandomEventCause implements IEventCause {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "global_random");
    public static final MapCodec<GlobalRandomEventCause> CODEC = MapCodec.unit(new GlobalRandomEventCause());

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }
}
