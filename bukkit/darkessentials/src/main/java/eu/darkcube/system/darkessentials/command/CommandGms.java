/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.GamemodeChanger;

public class CommandGms extends Command {
	public CommandGms() {
		super(DarkEssentials.getInstance(), "gms", new Command[0], "Setzt Spieler in den Survival-Mode.",
				new Argument("Spieler", "Der Spieler, dessen Gamemode geändert werden soll.", false));
		setAliases("d_gms", "gm0");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0 && !(sender instanceof Player)) {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
			return true;
		}
		GamemodeChanger.changeGamemode(0, sender, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length != 0) {
			return DarkEssentials.getPlayersStartWith(args);
		}
		return new ArrayList<>();
	}
}
