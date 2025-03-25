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

import java.util.concurrent.CompletableFuture;

public class EventCommand {
    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext context, Commands.CommandSelection selection) {

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("event")
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
            context.getSource().sendSystemMessage(
                    Component.translatable("votvevents.commands.event.start.not_found", eventLoc.toString())
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        var eventContext = new EventContext(context.getSource().getServer())
                .withPlayer(context.getSource().getPlayer());

        // TODO: handle if event failed (it would be null)
        var activeEvent = VotvEvents.getEventManager().runEvent(event, eventContext, isForced);

        // Sends a success message, with the event name and description as hover text
        context.getSource().sendSuccess(
                () -> Component.translatable("votvevents.commands.event.start.success" + (isForced ? ".force" : ""),
                        Component.literal(event.getName()).withStyle(style ->
                                style.withUnderlined(true)
                                        .withHoverEvent(
                                                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        Component.literal(event.getDescription()))
                                        )
                        )),
                true);


        return 1;
    }

    /**
     * Executes the print command
     */
    private static int executePrintEvents(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.translatable("votvevents.commands.event.print",
                        ComponentUtils.formatList(VotvEvents.getEventManager().getRegisteredEvents(),
                                (e) -> Component.literal(" - " + e))),
                false);

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
                .map(ResourceLocation::toString)
                .filter(e -> e.startsWith(input))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

