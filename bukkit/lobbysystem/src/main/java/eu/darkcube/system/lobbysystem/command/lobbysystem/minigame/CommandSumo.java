/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame;

import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.sumo.CommandAddTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.sumo.CommandListTasks;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.sumo.CommandRemoveTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.sumo.CommandSetSpawn;

public class CommandSumo extends LobbyCommandExecutor {

	public CommandSumo() {
		super("sumo", b -> {
			b.then(new CommandAddTask().builder()).then(new CommandListTasks().builder())
					.then(new CommandRemoveTask().builder()).then(new CommandSetSpawn().builder());
		});
	}

}
