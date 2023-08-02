/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.util.Language;

import java.util.Collection;

public interface ICommandExecutor extends Audience {

    default void sendMessage(BaseMessage message, Object... components) {
        this.sendMessage(message.getMessage(this, components));
    }

    default void sendCompletions(String commandLine, Collection<Suggestion> completions) {
        for (Suggestion completion : completions) {
            String cmd = getCommandPrefix() + completion.apply(commandLine);
            Component clickable = Component.text(completion.getText()).color(NamedTextColor.AQUA).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));

            Component hover = Component.empty();
            if (completion.getTooltip() != null && completion.getTooltip().getString() != null) hover = hover.append(Component.text(completion.getTooltip().getString())).append(Component.newline());
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
