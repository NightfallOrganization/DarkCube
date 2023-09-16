/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.pserver.CommandMigrateAll;
import eu.darkcube.system.lobbysystem.command.lobbysystem.pserver.CommandRegistry;

public class CommandPServer extends LobbyCommandExecutor {
    public CommandPServer(Lobby lobby) {
        super("pserver", b -> b.then(new CommandRegistry(lobby).builder()).then(new CommandMigrateAll().builder()));
    }
}
