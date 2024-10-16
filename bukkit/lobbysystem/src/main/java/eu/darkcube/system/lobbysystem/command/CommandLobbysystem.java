/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.lobbysystem.*;

public class CommandLobbysystem extends LobbyCommand {

    public CommandLobbysystem(Lobby lobby) {
        super("lobbysystem", b -> b
                .then(new CommandShowSkullCache().builder())
                .then(new CommandSetSpawn().builder())
                .then(new CommandMinigame().builder())
                .then(new CommandNPC().builder())
                .then(new CommandBuild().builder())
                .then(new CommandSetFisherSpawn().builder())
                .then(new CommandBorder().builder())
                .then(new CommandJumpAndRun().builder())
                .then(new CommandPServer(lobby).builder())
                .then(new CommandWinter().builder()));
    }

}
