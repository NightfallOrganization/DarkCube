/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.command.smash.CommandLobby;
import eu.darkcube.system.commandapi.Command;

public class CommandSmash extends Command {

	public CommandSmash() {
		super(Main.getInstance(), "smash", new Command[] {
				new CommandLobby()
		}, "Smash Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}
}
