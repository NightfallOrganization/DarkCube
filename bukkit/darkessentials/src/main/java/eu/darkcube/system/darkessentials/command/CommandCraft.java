/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;

public class CommandCraft extends Command {

	public CommandCraft() {
		super(Main.getInstance(), "craft", new Command[0], "Öffnet ein Craftingfenster.");
		setAliases("d_craft");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 0) {
			return false;
		}
		if (!(sender instanceof Player)) {
			Main.getInstance().sendMessage(Main.cFail() + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
		}
		((Player) sender).openWorkbench(((Player) sender).getLocation(), true);
		return true;
	}

}
