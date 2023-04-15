/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.command.woolbattle.*;
import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.system.commandapi.Command;

public class CommandWoolBattle extends WBCommandExecutor {

	public CommandWoolBattle() {
		super("woolbattle",
				b -> b.then(new CommandTeam().builder()).then(new CommandCreateTeam().builder())
						.then(new CommandDeleteTeam().builder())
						.then(new CommandListTeams().builder())
						.then(new CommandCreateMap().builder())
						.then(new CommandDeleteMap().builder()).then(new CommandMap().builder())
						.then(new CommandListMaps().builder())
						.then(new CommandLoadWorld().builder())
						.then(new CommandSetSpawn().builder()));
	}

}
