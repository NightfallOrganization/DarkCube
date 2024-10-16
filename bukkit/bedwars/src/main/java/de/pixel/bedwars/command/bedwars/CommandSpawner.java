/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.command.bedwars;

import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.command.bedwars.spawner.CommandCreate;
import de.pixel.bedwars.command.bedwars.spawner.CommandDelete;
import de.pixel.bedwars.command.bedwars.spawner.CommandList;
import eu.darkcube.system.commandapi.Command;

public class CommandSpawner extends Command {

	public CommandSpawner() {
		super(Main.getInstance(), "spawner", new Command[] {
				new CommandList(), new CommandCreate(), new CommandDelete()
		}, "Spawner Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender arg0, String[] arg1) {
		return false;
	}
}
