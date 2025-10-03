package ru.maxthetomas.e418.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.ActiveEvent;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.EventResource;
import ru.maxthetomas.e418.event.cause.impl.ConsoleCommandEventCause;
import ru.maxthetomas.e418.event.registry.EventRegistries;
import ru.maxthetomas.e418.util.storage.data.PlayerData;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameReturnValue")
public class EventCommand {
    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext context, Commands.CommandSelection selection) {

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("event")
                .requires(s -> s.hasPermission(2)) // "Game Master" (docs: https://minecraft.wiki/w/Permission_level)
                .then(startEvent())
                .then(queueEvent())
                .then(printEvents())
                .then(stopEvent())
        );
    }


    /**
     * Creates an argument tree for the /event start command
     */
    private static LiteralArgumentBuilder<CommandSourceStack> startEvent() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("start")
                .then(
                        RequiredArgumentBuilder.<CommandSourceStack, ResourceLocation>argument("event", ResourceLocationArgument.id())
                                .executes(EventCommand::executeStartSubcommand)
                                .suggests(EventCommand::getEventSuggestions)
                                .then(
                                        Commands.literal("force")
                                                .executes(EventCommand::executeStartSubcommand)
                                )
                                .then(
                                        Commands.literal("check")
                                                .executes(EventCommand::executeStartSubcommand)
                                )
                );
    }

    /**
     * Creates an argument tree for /event stop
     */
    private static LiteralArgumentBuilder<CommandSourceStack> stopEvent() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("stop")
                .then(
                        RequiredArgumentBuilder.<CommandSourceStack, ResourceLocation>argument("event", ResourceLocationArgument.id())
                                .executes(EventCommand::executeStopSubcommand)
                                .suggests(EventCommand::getActiveEventSuggestions)
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("dispose")
                                        .executes(EventCommand::executeStopSubcommand))
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("all")
                                .executes(EventCommand::executeClear)
                );
    }

    /**
     * Creates an argument tree for /event print delays
     */
    private static LiteralArgumentBuilder<CommandSourceStack> eventRandomDelays() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("print")
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("delays")
                                .executes(EventCommand::executePrintRandomEventDelays)
                );
    }

    /**
     * Creates an argument tree for the /event queue command
     */
    private static LiteralArgumentBuilder<CommandSourceStack> queueEvent() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("queue")
                .then(
                        RequiredArgumentBuilder.<CommandSourceStack, ResourceLocation>argument("event", ResourceLocationArgument.id())
                                .executes(EventCommand::executeQueueNoTimeoutSubcommand)
                                .suggests(EventCommand::getEventSuggestions)
                                .then(
                                        RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("timeout", IntegerArgumentType.integer(1))
                                                .executes(EventCommand::executeQueueSubcommand)
                                                .then(
                                                        Commands.literal("force")
                                                                .executes(EventCommand::executeQueueSubcommand)
                                                )
                                )
                                .then(
                                        Commands.literal("force")
                                                .executes(EventCommand::executeQueueNoTimeoutSubcommand)
                                )

                );
    }

    /**
     * Prints out selected data
     */
    private static LiteralArgumentBuilder<CommandSourceStack> printEvents() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("print")
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("registered")
                                .executes(EventCommand::executePrintRegisteredEvents)
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("queued")
                                .executes(EventCommand::executePrintQueuedEvents)
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("active")
                                .executes(EventCommand::executePrintActiveEvents)
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("event_registry")
                                .executes(EventCommand::executePrintEventRegistriesSummary)
                                .then(
                                        RequiredArgumentBuilder.<CommandSourceStack, ResourceLocation>argument("registry", ResourceLocationArgument.id())
                                                .suggests(EventCommand::getEventRegistriesSuggestions)
                                                .executes(EventCommand::executePrintEventRegistryEvents)
                                )
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("events_by_tag")
                                .executes(EventCommand::executePrintEventRegistriesSummary)
                                .then(
                                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("tag", StringArgumentType.string())
                                                .executes(EventCommand::executePrintEventsByTagInRegistry)
                                )
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("delays")
                                .executes(EventCommand::executePrintRandomEventDelays)
                );
    }

    /**
     * Executes the /event start command with all possible arguments
     */
    private static int executeStartSubcommand(CommandContext<CommandSourceStack> context) {
        var eventLoc = ResourceLocationArgument.getId(context, "event");
        var isForced = context.getNodes().getLast().getNode().getName().equals("force");
        var isDryRun = context.getNodes().getLast().getNode().getName().equals("check");

        var manager = E418.getEventManager();
        var event = manager.getEvent(eventLoc);

        if (event == null) {
            context.getSource().sendFailure(
                    Component.translatable("e418.commands.event.start.not_found", eventLoc.toString())
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        var eventContext = new EventContext(context.getSource().getServer())
                .withPlayer(context.getSource().getPlayer())
                .withCause(new ConsoleCommandEventCause(context))
                .withForced(isForced);

        if (isDryRun) { 
            return executeStartCheck(context, event, eventContext);
        }

        var activeEvent = E418.getEventManager().runEvent(event, eventContext);

        if (activeEvent == null) {
            context.getSource().sendFailure(
                    Component.translatable("e418.commands.event.start.fail", formatEvent(event))
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        // Sends a success message, with the event name and description as hover text
        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.start.success" +
                                (isForced ? ".force" : ""),
                        formatEvent(event)), true);

        return 1;
    }

    private static int executeStartCheck(CommandContext<CommandSourceStack> context, EventResource eventResource, EventContext eventContext) { 
        var run = eventResource.runConditions();
        var queue = eventResource.queueConditions();

        if (queue.size() == 0 && run.size() == 0) { 
            context.getSource().sendFailure(Component
                .translatable("e418.commands.event.start.check.no_conditions")
                .withStyle(ChatFormatting.RED));
            return 0;
        }

        if (run.size() > 0) { 
            var runConditions = ComponentUtils.formatList(run,
                Component.literal("\n"),
                (e) -> Component.literal(" - ") .append(
                        Component.literal(e.getType().toString())
                        .withStyle(e.check(eventContext) ? ChatFormatting.GREEN : ChatFormatting.RED))
                );

            context.getSource().sendSuccess(() -> Component.translatable("e418.commands.event.start.check.run")
                .append(Component.literal("\n")).append(runConditions), false);;
        }


        if (queue.size() > 0) { 
            var runConditions = ComponentUtils.formatList(queue,
                Component.literal("\n"),
                (e) -> Component.literal(" - ") .append(
                        Component.literal(e.getType().toString())
                        .withStyle(e.check(eventContext) ? ChatFormatting.GREEN : ChatFormatting.RED))
                );

            context.getSource().sendSuccess(() -> Component.translatable("e418.commands.event.start.check.queue")
                .append(Component.literal("\n")).append(runConditions), false);
        }

        return 1;
    }

    /**
     * Executes the /event queue command with timeout
     */
    private static int executeQueueSubcommand(CommandContext<CommandSourceStack> context) {
        var eventLoc = ResourceLocationArgument.getId(context, "event");
        var timeout = IntegerArgumentType.getInteger(context, "timeout");
        var isForced = context.getNodes().getLast().getNode().getName().equals("force");

        var manager = E418.getEventManager();
        var event = manager.getEvent(eventLoc);

        if (event == null) {
            context.getSource().sendFailure(
                    Component.translatable("e418.commands.event.start.not_found", eventLoc.toString())
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        var eventContext = new EventContext(context.getSource().getServer())
                .withPlayer(context.getSource().getPlayer())
                .withCause(new ConsoleCommandEventCause(context))
                .withForced(isForced);


        var isQueued = E418.getEventManager().queueEvent(event, eventContext, timeout);

        if (!isQueued) {
            context.getSource().sendFailure(
                    Component.translatable("e418.commands.event.queue.fail", formatEvent(event))
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        // Sends a success message, with the event name and description as hover text
        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.queue.success" +
                                (isForced ? ".force" : ""),
                        formatEvent(event)), true);

        return 1;
    }

    /**
     * Executes the /event queue command without timeout
     */
    private static int executeQueueNoTimeoutSubcommand(CommandContext<CommandSourceStack> context) {
        var eventLoc = ResourceLocationArgument.getId(context, "event");
        var isForced = context.getNodes().getLast().getNode().getName().equals("force");

        var manager = E418.getEventManager();
        var event = manager.getEvent(eventLoc);

        if (event == null) {
            context.getSource().sendFailure(
                    Component.translatable("e418.commands.event.start.not_found", eventLoc.toString())
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        var eventContext = new EventContext(context.getSource().getServer())
                .withPlayer(context.getSource().getPlayer())
                .withCause(new ConsoleCommandEventCause(context))
                .withForced(isForced);


        var isQueued = E418.getEventManager().queueEvent(event, eventContext);

        if (!isQueued) {
            context.getSource().sendFailure(
                    Component.translatable("e418.commands.event.queue.fail", formatEvent(event))
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        // Sends a success message, with the event name and description as hover text
        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.queue.success" +
                                (isForced ? ".force" : ""),
                        formatEvent(event)), true);

        return 1;
    }

    /**
     * Prints registered events
     */
    private static int executePrintRegisteredEvents(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.print",
                        ComponentUtils.formatList(E418.getEventManager().getRegisteredEvents(),
                                Component.literal("\n"),
                                (e) -> Component.literal(" - " + e.toString()))
                ), false);

        return 1;
    }

    /**
     * Prints queued events
     */
    private static int executePrintQueuedEvents(CommandContext<CommandSourceStack> context) {
        // TODO: make it also show timeout
        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.print_queued",
                        ComponentUtils.formatList(E418.getEventManager().getQueuedEvents(),
                                Component.literal("\n"),
                                (e) -> Component.literal(" - ").append(formatEvent(e.resource())))
                ), false);

        return 1;
    }

    /**
     * Prints active events
     */
    private static int executePrintActiveEvents(CommandContext<CommandSourceStack> context) {
        // TODO: make it also show timeout
        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.print_active",
                        ComponentUtils.formatList(E418.getEventManager().getActiveEvents(),
                                Component.literal("\n"),
                                (e) -> Component.literal(" - ").append(formatEvent(e.resource)))
                ), false);

        return 1;
    }

    /**
     * Prints events from an event registry
     */
    private static int executePrintEventRegistriesSummary(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.print_event_registries_summary",
                        ComponentUtils.formatList(EventRegistries.getRegistries(),
                                Component.literal("\n"),
                                (e) -> Component.translatable("e418.commands.event.print_event_registries_summary.line",
                                        e.toString(), EventRegistries.get(e).getEvents().size()
                                ))
                ), false);

        return 1;
    }

    /**
     * Prints events from an event registry
     */
    private static int executePrintEventRegistryEvents(CommandContext<CommandSourceStack> context) {
        var registryKey = ResourceLocationArgument.getId(context, "registry");
        var registry = EventRegistries.get(registryKey);

        if (registry == null) {
            context.getSource().sendFailure(Component.translatable("e418.commands.event.invalid_registry", registryKey.toString())
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.print_event_registry",
                        Component.literal(registryKey.toString()),
                        ComponentUtils.formatList(registry.getEvents(),
                                Component.literal("\n"),
                                (e) -> Component.translatable("e418.commands.event.print_event_registry.line", formatEvent(e.element()), e.weight())),
                        ComponentUtils.formatList(registry.getTags(),
                                Component.literal("\n"),
                                (e) -> Component.literal(" - ").append(e))
                ), false);

        return 1;
    }

    /**
     * Prints events from registries with tag
     */
    private static int executePrintEventsByTagInRegistry(CommandContext<CommandSourceStack> context) {
        var tag = StringArgumentType.getString(context, "tag");
        var events = EventRegistries.getEventsWithTag(tag);

        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.print_events_by_tag",
                        tag,
                        ComponentUtils.formatList(events.values,
                                Component.literal("\n"),
                                (e) -> Component.translatable("e418.commands.event.print_events_by_tag.line", formatEvent(e.element()), e.weight()))
                ), false);

        return 1;
    }

    /**
     * Prints event delays from random event manager
     */
    private static int executePrintRandomEventDelays(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.print_event_delays",
                        E418.getEventEngine().RandomEventManager.GlobalEventTick,
                        ComponentUtils.formatList(context.getSource().getServer().getPlayerList().getPlayers(),
                                Component.literal("\n"),
                                (e) -> {
                                    var data = PlayerData.ensureData(e);
                                    return Component.translatable("e418.commands.event.print_event_delays.line",
                                            e.getFeedbackDisplayName(), data.eventTimestamp, data.eventUnlockTimestamp);
                                })
                ), false);
        return 1;
    }

    private static int executeClear(CommandContext<CommandSourceStack> context) {
        E418.getEventManager().getQueuedEvents().forEach((event) -> E418.getEventManager().dequeueEvent(event));
        E418.getEventManager().getActiveEvents().forEach((event) -> E418.getEventManager().stopEvent(event));

        context.getSource().sendSuccess(
                () -> Component.translatable("e418.commands.event.stop.all"), false);
        return 1;
    }

    private static int executeStopSubcommand(CommandContext<CommandSourceStack> context) {
        var isForced = context.getNodes().getLast().getNode().getName().equals("dispose");

        var eventLoc = ResourceLocationArgument.getId(context, "event");
        var manager = E418.getEventManager();

        int count = 0;

        for (ActiveEvent d : manager.getActiveEvents()) {
            if (eventLoc.equals(manager.getResourceLocation(d))) {
                manager.stopEvent(d);
                if (isForced)
                    manager.disposeEvent(d);
                count++;
            }
        }

        int finalCount = count;
        if (count > 0) {
            context.getSource().sendSuccess(() ->
                    Component.translatable("e418.commands.event.stop.success",
                            finalCount), true);
        } else {
            context.getSource().sendFailure(
                    Component.translatable("e418.commands.event.stop.fail").withStyle(ChatFormatting.RED));
        }

        return count;
    }


    /**
     * Suggests event names for every event that starts with the input
     */
    private static CompletableFuture<Suggestions> getActiveEventSuggestions(CommandContext<CommandSourceStack> ctx,
                                                                            SuggestionsBuilder builder) {
        var input = builder.getRemaining();

        E418.getEventManager().getActiveEvents()
                .stream()
                .filter(e -> e.toString().startsWith(input))
                .forEach((e) -> builder.suggest(E418.getEventManager().getResourceLocation(e).toString(),
                        Component.literal(e.resource.description())));

        return builder.buildFuture();
    }

    /**
     * Suggests event registry names for every event that starts with the input
     */
    private static CompletableFuture<Suggestions> getEventRegistriesSuggestions(CommandContext<CommandSourceStack> ctx,
                                                                                SuggestionsBuilder builder) {
        var input = builder.getRemaining();

        EventRegistries.getRegistries()
                .stream()
                .filter(e -> e.toString().startsWith(input))
                .forEach((e) -> builder.suggest(e.toString()));

        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> getEventSuggestions(CommandContext<CommandSourceStack> ctx,
                                                                      SuggestionsBuilder builder) {
        var input = builder.getRemaining();

        E418.getEventManager().getRegisteredEvents()
                .stream()
                .filter(e -> e.toString().startsWith(input))
                .forEach((e) -> builder.suggest(e.toString(), Component.literal(E418.getEventManager().getEvent(e).description())));

        return builder.buildFuture();
    }

    // Util functions
    private static Component formatEvent(EventResource event) {
        return Component.literal(event.name()).withStyle(style ->
                style.withUnderlined(true)
                        .withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Component.literal(event.description()))
                        )
        );
    }
}

