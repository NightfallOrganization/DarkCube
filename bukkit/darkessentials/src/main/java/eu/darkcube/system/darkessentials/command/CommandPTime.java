/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;

public class CommandPTime extends Command {

	public CommandPTime() {
		super(DarkEssentials.getInstance(), "ptime", new Command[0], "Setzt die Zeit nur für dich selbst.",
				new Argument("Uhrzeit", "Die Uhrzeit, die du für dich gesetzt wird."));
		setAliases("d_ptime");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reset")) {
				((Player) sender).resetPlayerTime();
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Zeit zurückgesetzt", sender);
				return true;
			}
			int time;
			try {
				time = Integer.parseInt(args[0]);
			} catch (Exception ex) {
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst eine Zahl angeben!", sender);
				return true;
			}
			((Player) sender).setPlayerTime(time, true);
			DarkEssentials.getInstance().sendMessage(new StringBuilder().append(DarkEssentials.cConfirm()).append("Zeit auf ")
					.append(DarkEssentials.cValue()).append(time).append(DarkEssentials.cConfirm()).append(" gesetzt.").toString(), sender);
		}
		return false;
	}

}
