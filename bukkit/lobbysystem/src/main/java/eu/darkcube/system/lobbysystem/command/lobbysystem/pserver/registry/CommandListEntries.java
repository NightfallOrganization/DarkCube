/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.pserver.registry;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;

public class CommandListEntries extends LobbyCommand {
    public CommandListEntries(Lobby lobby) {
        super("listEntries", b -> b.executes(ctx -> {
            var entries = lobby.gameRegistry().entries();
            Component component = Component.text("Entries: (" + entries.size() + ")", NamedTextColor.GRAY);
            for (var entriesEntry : entries.entrySet()) {
                component = component
                        .appendNewline()
                        .append(Component.text(" - ", NamedTextColor.GRAY))
                        .append(Component.text(entriesEntry.getKey(), NamedTextColor.AQUA));
                for (var entry : entriesEntry.getValue()) {
                    component = component
                            .appendNewline()
                            .append(Component.text("    -> ", NamedTextColor.GRAY))
                            .append(Component.text(entry.data()));
                }
            }
            ctx.getSource().sendMessage(component);
            return 0;
        }));
    }
}
