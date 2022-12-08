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

public class CommandCreateTeam extends Command {

	private static final String[] TEAM_NAMES = new String[] { "RED", "BLUE", "YELLOW", "PURPLE", "CYAN", "GREEN",
			"LIME", "ORANGE", "LIGHT_BLUE", "PINK", "GRAY", "LIGHT_GRAY", "BLACK" };

	public CommandCreateTeam() {
		super(Main.getInstance(), "createTeam", new Command[0], "Erstelle ein Team",
				new Argument("team", "Der Teamname"));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(TEAM_NAMES, args[0].toUpperCase());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			String name = args[0].toUpperCase();
			if (!Arrays.asList(TEAM_NAMES).contains(name)) {
				sender.sendMessage("§cUngültiger Name!");
				return true;
			}
			for (String teamname : Team.getAllTeamNames()) {
				if (teamname.equals(name)) {
					sender.sendMessage("§cDieses Team existiert bereits!");
					return true;
				}
			}
			Team team = Team.createTeam(name);
			sender.sendMessage("§7Du hast das Team §" + team.getNamecolor() + team.getTranslationName() + " erstellt!");
			return true;
		}
		return false;
	}
}
