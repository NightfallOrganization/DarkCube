/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.command.bedwars.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.command.bedwars.CommandTeam;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Arrays;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandEnable extends SubCommand {

	public CommandEnable() {
		super(Main.getInstance(), "enable", new Command[0], "Aktiviert ein Team");
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(ChatColor.values(), args[0].toUpperCase());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Team team = CommandTeam.doTeam(getSpaced(), sender);
		if (team == null) {
			return true;
		}
		if (team.isEnabled()) {
			sender.sendMessage("§cDieses Team ist bereits aktiviert!");
			return true;
		}
		team.setEnabled(true);
		sender.sendMessage("§aDu hast das Team aktiviert!");
		return true;
	}

}
