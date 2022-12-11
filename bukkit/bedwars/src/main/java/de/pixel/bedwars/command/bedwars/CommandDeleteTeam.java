/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.command.bedwars;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Arrays;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;

public class CommandDeleteTeam extends Command {

	public CommandDeleteTeam() {
		super(Main.getInstance(), "deleteTeam", new Command[0], "Löscht ein Team",
				new Argument("team", "Der Teamname"));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(Team.getAllTeams(), args[0].toUpperCase());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			String name = args[0].toUpperCase();
			if (!Team.getAllTeamNames().contains(name)) {
				sender.sendMessage("§cUnbekanntes Team!");
				return true;
			}
			for (Team team : Team.getAllTeams()) {
				if (team.getTranslationName().equals(name)) {
					if (team.isDeleted()) {
						sender.sendMessage("§cUnbekanntes Team!");
						return true;
					}
					sender.sendMessage("§aTeam gelöscht!");
					team.deleteFile();
					return true;
				}
			}
		}
		return false;
	}
}
