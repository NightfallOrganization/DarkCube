/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame;

import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandAddTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandListTasks;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandRemoveTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandSetSpawn;

public class CommandWoolBattle extends LobbyCommand {

	public CommandWoolBattle() {
		super("woolbattle", b -> {
			b.then(new CommandAddTask().builder()).then(new CommandListTasks().builder())
					.then(new CommandRemoveTask().builder()).then(new CommandSetSpawn().builder());
		});
	}

}
