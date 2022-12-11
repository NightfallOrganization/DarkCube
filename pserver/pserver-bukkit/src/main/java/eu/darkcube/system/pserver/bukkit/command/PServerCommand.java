/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit.command;

import org.bukkit.command.CommandSender;

public interface PServerCommand {

	boolean execute(CommandSender sender, String[] args);
	
}
