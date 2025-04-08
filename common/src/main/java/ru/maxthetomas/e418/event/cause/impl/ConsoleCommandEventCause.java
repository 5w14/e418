package ru.maxthetomas.e418.event.cause.impl;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import ru.maxthetomas.e418.event.cause.IEventCause;

public class ConsoleCommandEventCause implements IEventCause {

    public final CommandContext<CommandSourceStack> context;

    public ConsoleCommandEventCause(CommandContext<CommandSourceStack> context) {
        this.context = context;
    }
}
