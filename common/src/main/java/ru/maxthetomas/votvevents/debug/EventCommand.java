package ru.maxthetomas.votvevents.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.EventResource;

import java.util.concurrent.CompletableFuture;

public class EventCommand {
    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext context, Commands.CommandSelection selection) {

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("event")
                .requires(s -> s.hasPermission(2)) // "Game Master" (docs: https://minecraft.wiki/w/Permission_level)
                .then(startSubcommand())
                .then(printEvents())
        );
    }


    /**
     * Creates a argument tree for the /event start command
     */
    private static LiteralArgumentBuilder<CommandSourceStack> startSubcommand() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("start")
                .then(
                        RequiredArgumentBuilder.<CommandSourceStack, ResourceLocation>argument("event", ResourceLocationArgument.id())
                                .executes(EventCommand::executeStartSubommand)
                                .suggests(EventCommand::getEventSuggestions)
                                .then(
                                        Commands.literal("force")
                                                .executes(EventCommand::executeStartSubommand)
                                )
                );
    }

    /**
     * Prints out all loaded commands
     */
    private static LiteralArgumentBuilder<CommandSourceStack> printEvents() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("print")
                .executes(EventCommand::executePrintEvents);
    }

    /**
     * Executes the /event start command with all possible arguments
     */
    private static int executeStartSubommand(CommandContext<CommandSourceStack> context) {
        var eventLoc = ResourceLocationArgument.getId(context, "event");
        var isForced = context.getNodes().getLast().getNode().getName().equals("force");

        var manager = VotvEvents.getEventManager();
        var event = manager.getEvent(eventLoc);

        if (event == null) {
            context.getSource().sendFailure(
                    Component.translatable("votvevents.commands.event.start.not_found", eventLoc.toString())
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        var eventContext = new EventContext(context.getSource().getServer())
                .withPlayer(context.getSource().getPlayer())
                .withForced(isForced);

        var activeEvent = VotvEvents.getEventManager().runEvent(event, eventContext);

        if (activeEvent == null) {
            context.getSource().sendFailure(
                    Component.translatable("votvevents.commands.event.start.fail", formatEvent(event))
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        // Sends a success message, with the event name and description as hover text
        context.getSource().sendSuccess(
                () -> Component.translatable("votvevents.commands.event.start.success" +
                                (isForced ? ".force" : ""),
                        formatEvent(event)), true);

        return 1;
    }

    /**
     * Executes the print command
     */
    private static int executePrintEvents(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.translatable("votvevents.commands.event.print",
                        ComponentUtils.formatList(VotvEvents.getEventManager().getRegisteredEvents(),
                                Component.literal("\n"),
                                (e) -> Component.literal(" - " + e.toString()))
                ), false);

        return 1;
    }

    /**
     * Suggests event names for every event that starts with the input
     */
    private static CompletableFuture<Suggestions> getEventSuggestions(CommandContext<CommandSourceStack> ctx,
                                                                      SuggestionsBuilder builder) {
        var input = builder.getRemaining();

        VotvEvents.getEventManager().getRegisteredEvents()
                .stream()
                .filter(e -> e.toString().startsWith(input))
                .forEach((e) -> builder.suggest(e.toString(), Component.literal(VotvEvents.getEventManager().getEvent(e).getDescription())));

        return builder.buildFuture();
    }


    // Util functions
    private static Component formatEvent(EventResource event) {
        return Component.literal(event.getName()).withStyle(style ->
                style.withUnderlined(true)
                        .withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Component.literal(event.getDescription()))
                        )
        );
    }
}

