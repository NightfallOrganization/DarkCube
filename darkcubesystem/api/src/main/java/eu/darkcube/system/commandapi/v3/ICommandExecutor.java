/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.com.mojang.brigadier.CommandDispatcher;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContextBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.context.SuggestionContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.util.Language;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public interface ICommandExecutor extends Audience {

    default void sendMessage(BaseMessage message, Object... components) {
        this.sendMessage(message.getMessage(this, components));
    }

    default void sendCompletions(String commandLine, Collection<Suggestion> completions) {
        CommandDispatcher<CommandSource> dispatcher = CommandAPI.getInstance().getCommands().getDispatcher();
        ParseResults<CommandSource> parse = dispatcher.parse(commandLine, CommandSource.create(this));
        int cursor = parse.getReader().getTotalLength();
        final CommandContextBuilder<CommandSource> context = parse.getContext();

        final SuggestionContext<CommandSource> nodeBeforeCursor = context.findSuggestionContext(cursor);
        final CommandNode<CommandSource> parent = nodeBeforeCursor.parent;
        final int start = Math.min(nodeBeforeCursor.startPos, cursor);

        final String fullInput = parse.getReader().getString();
        final String truncatedInput = fullInput.substring(0, cursor);
        final String truncatedInputLowerCase = truncatedInput.toLowerCase(Locale.ROOT);

        @SuppressWarnings("unchecked") final CompletableFuture<Suggestions>[] futures = new CompletableFuture[parent.getChildren().size()];
        int i = 0;
        for (final CommandNode<CommandSource> node : parent.getChildren()) {
            CompletableFuture<Suggestions> future = Suggestions.empty();
            try {
                future = node.listSuggestions(context.build(truncatedInput), new SuggestionsBuilder(truncatedInput, truncatedInputLowerCase, start));
            } catch (final CommandSyntaxException ignored) {
            }
            futures[i++] = future;
        }
        CompletableFuture.allOf(futures).thenRun(() -> {
            for (CompletableFuture<Suggestions> future : futures) {
                Suggestions sug = future.getNow(null);
                for (Suggestion suggestion : sug.getList()) {
                    sendMessage(Component.text("sug: " + suggestion.getText()));
                }
            }
        });

        for (Suggestion completion : completions) {
            String cmd = getCommandPrefix() + completion.apply(commandLine);
            Component clickable = Component.text(completion.getText()).color(NamedTextColor.AQUA).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));

            Component hover = Component.empty();
            if (completion.getTooltip() != null && completion.getTooltip().getString() != null)
                hover = hover.append(Component.text(completion.getTooltip().getString())).append(Component.newline());
            hover = hover.append(Component.text("Click to insert command", NamedTextColor.GRAY).append(Component.newline()).append(Component.text(cmd, NamedTextColor.GRAY)));

            clickable = clickable.hoverEvent(HoverEvent.showText(hover));

            Component c = Component.text(" - ").color(NamedTextColor.GREEN).append(clickable);
            sendMessage(c);
        }
    }

    Language getLanguage();

    void setLanguage(Language language);

    default String getCommandPrefix() {
        return "";
    }
}
