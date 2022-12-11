/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun.*;
import eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun.CommandSetSpawn;

public class CommandJumpAndRun extends LobbyCommandExecutor {

	public CommandJumpAndRun() {
		super("jumpAndRun", b -> b.then(new CommandListRegions().builder())
				.then(new CommandCreateRegion().builder()).then(new CommandDeleteRegion().builder())
				.then(new CommandSetSpawn().builder()).then(new CommandSetPlate().builder())
				.then(new CommandSetEnabled().builder()));
	}

}
