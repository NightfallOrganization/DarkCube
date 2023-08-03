/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.util.Language;

import java.util.*;
import java.util.stream.Collectors;

public interface ICommandExecutor extends Audience {

    default void sendMessage(BaseMessage message, Object... components) {
        this.sendMessage(message.getMessage(this, components));
    }

    default void sendCompletions(String commandLine, Collection<Suggestion> completions) {
        sendCompletions(commandLine, completions, new HashMap<>());
    }

    default void sendCompletions(String commandLine, Collection<Suggestion> completions, Map<CommandNode<CommandSource>, String> usages) {
        List<String> possibilities = new ArrayList<>();
        Map<String, String> usageMap = new HashMap<>();
        int idx = commandLine.lastIndexOf(' ');
        String lastWord = commandLine.substring(idx + 1) + " ";
        Map<String, CommandNode<CommandSource>> reverseUsages = new HashMap<>();
        for (Map.Entry<CommandNode<CommandSource>, String> entry : usages.entrySet()) {
            String usage = entry.getValue().replace('|', '┃');
            if (usage.length() > 30) {
                usage = usage.substring(0, 30);
                int i = usage.lastIndexOf(' ', 30);
                if (i != -1) {
                    usage = usage.substring(0, i);
                }
            }
            usageMap.put(entry.getKey().getName(), usage);
            reverseUsages.put(usage, entry.getKey());
            possibilities.add(entry.getKey().getName());
        }
        for (Suggestion completion : completions) possibilities.add(completion.getText());

        possibilities = possibilities.stream().distinct().sorted().collect(Collectors.toList());

        Map<String, Component> components = new HashMap<>();

//        final TextColor DARKER_AQUA_VALUE = TextColor.color(0x44EEEE);
        final TextColor DARKER_AQUA_VALUE = NamedTextColor.BLUE;

        for (Suggestion completion : completions) {
            String text = completion.getText();
            String cmd = getCommandPrefix() + completion.apply(commandLine);
            String display2 = null;
            if (usageMap.containsKey(text)) {
                String usage = usageMap.remove(text);
                display2 = usage.substring(text.length());
            }
            Component clickable = Component.text(text, NamedTextColor.AQUA);
            if (display2 != null) {
                clickable = clickable.append(Component.text(display2, DARKER_AQUA_VALUE));
            }

            clickable = clickable.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));

            Component hover = Component.empty();

            if (completion.getTooltip() != null && completion.getTooltip().getString() != null)
                hover = hover.append(Component.text(completion.getTooltip().getString())).append(Component.newline());

            hover = hover.append(Component
                    .text("Click to insert command", NamedTextColor.GRAY)
                    .append(Component.newline())
                    .append(Component.text(cmd, NamedTextColor.GRAY)));

            clickable = clickable.hoverEvent(HoverEvent.showText(hover));

            Component c = Component.text(" - ", NamedTextColor.GREEN).append(clickable);
            components.put(text, c);
        }

        for (Map.Entry<String, String> entry : usageMap.entrySet()) {
            String e = entry.getValue();
            if (!e.startsWith(lastWord)) {
                if (reverseUsages.get(e) instanceof LiteralCommandNode) {
                    continue;
                }
//                int i = e.indexOf(' ');
//                e = e.substring(i + 1);
            } else {
                e = e.substring(lastWord.length());
            }
            Component c = Component.text(" - ", NamedTextColor.GREEN).append(Component.text(e, DARKER_AQUA_VALUE));
            components.put(entry.getKey(), c);
        }
        for (String possibility : possibilities) {
            Component component = components.get(possibility);
            if (component != null) sendMessage(component);
        }
    }

    Language getLanguage();

    void setLanguage(Language language);

    default String getCommandPrefix() {
        return "";
    }
}
