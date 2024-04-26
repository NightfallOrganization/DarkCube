/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.command;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.world.GameWorld;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattleApi;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.suggestion.SuggestionCallback;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class MinestomWoolBattleCommands extends CommonWoolBattleCommands {
    private final Map<String, Command> wrappers = new ConcurrentHashMap<>();
    private final MinestomWoolBattleApi woolbattle;
    private final SuggestionCallback suggestionCallback = (sender, context, suggestion) -> {
        var source = sourceFor(sender);
        var commandLine = suggestion.getInput();
        // \00 is minestom internal command shit. I don't like it, but it's what I have to work with
        if (commandLine.endsWith(" \00")) {
            commandLine = commandLine.substring(0, commandLine.length() - 1);
        }
        var parse = dispatcher.parse(commandLine, source);
        var suggestions = getTabCompletionsSync(parse);
        for (var s : suggestions.getList()) {
            Component tooltip = s.getTooltip() != null ? Component.text(s.getTooltip().getString()) : null;
            suggestion.addEntry(new SuggestionEntry(s.getText(), tooltip));
        }
    };
    private final CommandExecutor executor = (sender, context) -> {
        var commandLine = context.getInput();
        var source = sourceFor(sender);
        var parse = dispatcher.parse(commandLine, source);
        try {
            dispatcher.execute(parse);
        } catch (CommandSyntaxException ex) {
            var failedCursor = ex.getCursor();
            if (failedCursor == 0) {
                return; // Happens when someone tries to execute a command that requires a condition which is not met
            }
            if (failedCursor == -1) {
                return; // When there is no context to the exception. Use #createWithContext to work around this
            }

            source.sendMessage(text(ex.getMessage(), NamedTextColor.RED));

            if (failedCursor == commandLine.length()) {
                var commandLineNext = commandLine + " ";
                var parse2 = dispatcher.parse(commandLineNext, source);
                getTabCompletions(parse2).thenAccept(completions -> sendCompletions(source, commandLineNext, completions, usages(parse2)));
            } else {
                getTabCompletions(parse).thenAccept(completions -> sendCompletions(source, commandLine, completions, usages(parse)));
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            source.sendMessage(text("An error occurred while attempting to execute the command", NamedTextColor.RED));
        }
    };
    private final CommandManager commandManager;

    public MinestomWoolBattleCommands(MinestomWoolBattleApi woolbattle, CommandManager commandManager) {
        this.woolbattle = woolbattle;
        this.commandManager = commandManager;
    }

    private Map<CommandNode<CommandSource>, String> usages(ParseResults<CommandSource> parse) {
        var suggestionContext = parse.getContext().findSuggestionContext(parse.getReader().getTotalLength());
        return dispatcher.getSmartUsage(suggestionContext.parent, parse.getContext().getSource());
    }

    private void sendCompletions(CommandSource source, String commandLine, Suggestions suggestions, Map<CommandNode<CommandSource>, String> usages) {
        var possibilities = (List<String>) new ArrayList<String>();
        var usageMap = new HashMap<String, String>();
        var idx = commandLine.lastIndexOf(' ');
        var lastWord = commandLine.substring(idx + 1) + " ";
        var reverseUsages = new HashMap<String, CommandNode<CommandSource>>();
        for (var entry : usages.entrySet()) {
            var usage = entry.getValue();
            if (usage.length() > 30) {
                usage = usage.substring(0, 30);
                var i = usage.lastIndexOf(' ');
                if (i != -1) usage = usage.substring(0, i);
            }
            usageMap.put(entry.getKey().getName(), usage);
            reverseUsages.put(usage, entry.getKey());
            possibilities.add(entry.getKey().getName());
        }
        for (var completion : suggestions.getList()) {
            possibilities.add(completion.getText());
        }
        possibilities = possibilities.stream().distinct().sorted().toList();

        var components = new HashMap<String, eu.darkcube.system.libs.net.kyori.adventure.text.Component>();

        for (var completion : suggestions.getList()) {
            var text = completion.getText();
            var cmd = source.commandPrefix() + completion.apply(commandLine);
            String display2 = null;
            if (usageMap.containsKey(text)) {
                var usage = usageMap.remove(text);
                display2 = usage.substring(text.length());
            }
            var clickable = text(text, NamedTextColor.AQUA);
            if (display2 != null) {
                clickable = clickable.append(text(display2, NamedTextColor.BLUE));
            }

            clickable = clickable.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));

            var hover = empty();
            if (completion.getTooltip() != null && completion.getTooltip().getString() != null) {
                hover = hover.append(text(completion.getTooltip().getString())).append(newline());
            }

            hover = hover.append(text("Click to insert command", NamedTextColor.GRAY).append(newline()).append(text(cmd, NamedTextColor.GRAY)));

            clickable = clickable.hoverEvent(HoverEvent.showText(hover));

            var c = text(" - ", NamedTextColor.GREEN).append(clickable);
            components.put(text, c);
        }

        for (var entry : usageMap.entrySet()) {
            var e = entry.getValue();
            if (!e.startsWith(lastWord)) {
                if (reverseUsages.get(e) instanceof LiteralCommandNode<CommandSource>) {
                    continue;
                }
            } else {
                e = e.substring(lastWord.length());
            }
            var c = text(" - ", NamedTextColor.GREEN).append(text(e, NamedTextColor.BLUE));
            components.put(entry.getKey(), c);
        }
        for (var possibility : possibilities) {
            var component = components.get(possibility);
            if (component != null) {
                source.sendMessage(component);
            }
        }
    }

    private Suggestions getTabCompletionsSync(ParseResults<CommandSource> parse) {
        try {
            return getTabCompletions(parse).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Suggestions.empty().join();
    }

    private CompletableFuture<Suggestions> getTabCompletions(ParseResults<CommandSource> parse) {
        return dispatcher.getCompletionSuggestions(parse);
    }

    private CommandSource sourceFor(CommandSender sender) {
        @Nullable Game game = null;
        @UnknownNullability Vector3d pos = null;
        @UnknownNullability Vector2f rotation = null;
        @UnknownNullability World world = null;
        @NotNull String name = "unknown";
        @UnknownNullability String displayName = null;
        @NotNull String commandPrefix = "";
        @NotNull Map<String, Object> extra = new HashMap<>();
        @NotNull eu.darkcube.minigame.woolbattle.api.command.CommandSender customSender;
        if (sender instanceof MinestomPlayer player) {
            name = player.getUsername();
            var playerPos = player.getPosition();
            pos = new Vector3d(playerPos.x(), playerPos.y(), playerPos.z());
            rotation = new Vector2f(playerPos.yaw(), playerPos.pitch());
            world = woolbattle.woolbattle().worlds().get(player.getInstance());
            player.get(Pointer.pointer(CommandSource.class, Key.key("asd")));
            commandPrefix = "/";
            customSender = Objects.requireNonNullElseGet(player.user(), () -> new MinestomCommandSender(sender));
            var displayNameComponent = player.getDisplayName();
            if (world instanceof GameWorld gameWorld) game = gameWorld.game();
            if (displayNameComponent != null) displayName = PlainTextComponentSerializer.plainText().serialize(displayNameComponent);
        } else if (sender instanceof ConsoleSender) {
            name = "console";
            customSender = new MinestomCommandSender(sender);
        } else {
            customSender = new MinestomCommandSender(sender);
        }
        return new CommandSource(woolbattle, game, customSender, pos, rotation, world, name, displayName, extra, commandPrefix);
    }

    @Override
    protected void register0(WoolBattleCommand command, String name, LiteralCommandNode<CommandSource> node) {
        var wrapper = new Command(name);
        var argument = new MinestomArgumentWrapper("args");
        wrapper.setCondition((sender, commandString) -> {
            var wbSender = new MinestomCommandSender(sender);
            return wbSender.hasPermission(command.permission());
        });
        wrapper.setDefaultExecutor(executor);
        argument.setSuggestionCallback(suggestionCallback);
        wrapper.addSyntax(executor, argument);
        wrappers.put(name, wrapper);
        commandManager.register(wrapper);
    }

    @Override
    protected void unregister0(WoolBattleCommand command, String name, LiteralCommandNode<CommandSource> node) {
        var wrapper = wrappers.remove(name);
        if (wrapper == null) return;
        commandManager.unregister(wrapper);
    }
}
