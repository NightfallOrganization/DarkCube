/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.pserver;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.command.lobbysystem.pserver.registry.CommandAddEntry;
import eu.darkcube.system.lobbysystem.command.lobbysystem.pserver.registry.CommandListEntries;
import eu.darkcube.system.lobbysystem.command.lobbysystem.pserver.registry.CommandRemoveEntry;

public class CommandRegistry extends LobbyCommand {
    public CommandRegistry(Lobby lobby) {
        super("registry", b -> b
                .then(new CommandListEntries(lobby).builder())
                .then(new CommandAddEntry(lobby).builder())
                .then(new CommandRemoveEntry(lobby).builder()));
    }
}
