package ru.maxthetomas.e418.event.cause.impl;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.cause.IEventCause;

public record ConsoleCommandEventCause(CommandContext<CommandSourceStack> context) implements IEventCause {
    public static final ResourceLocation TYPE = E418.resLoc("console_command");
    // This is a unit codec because there are no instances of behaviours using
    // ConsoleCommand event cause, also there is no way to serialize CommandContext that I know of.
    public static final MapCodec<ConsoleCommandEventCause> CODEC = MapCodec.unit(new ConsoleCommandEventCause(null));

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }
}
