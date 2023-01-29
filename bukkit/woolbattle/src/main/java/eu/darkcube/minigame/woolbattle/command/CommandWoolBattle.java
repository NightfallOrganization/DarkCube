/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandCreateMap;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandCreateTeam;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandDeleteMap;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandDeleteTeam;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandListMaps;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandListTeams;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandLoadWorld;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandMap;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandSetSpawn;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandTeam;
import eu.darkcube.system.commandapi.Command;

public class CommandWoolBattle extends Command {

	public CommandWoolBattle() {
		super(WoolBattle.getInstance(), "woolbattle", new Command[] {
						new CommandTeam(), new CommandCreateTeam(), new CommandDeleteTeam(),
						new CommandListTeams(), new CommandCreateMap(), new CommandDeleteMap(),
						new CommandMap(), new CommandListMaps(), new CommandLoadWorld(),
						new CommandSetSpawn()
		}, "WoolBattle Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}

}
