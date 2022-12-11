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
import de.pixel.bedwars.command.bedwars.team.CommandDisable;
import de.pixel.bedwars.command.bedwars.team.CommandEnable;
import de.pixel.bedwars.command.bedwars.team.CommandSetNameColor;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Arrays;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.SpacedCommand;

public class CommandTeam extends SpacedCommand {

	public CommandTeam() {
		super(Main.getInstance(), "team", new SubCommand[] {
				new CommandSetNameColor(), new CommandEnable(), new CommandDisable()
		}, "Team Hauptcommand", new Argument("team", "Der Teamname"));
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
		return false;
	}
	
	public static Team doTeam(String spaced, CommandSender sender) {
		Team team = null;
		for(Team t : Team.getAllTeams()) {
			if(t.getTranslationName().equals(spaced)) {
				team = t;
				break;
			}
		}
		if(team == null) {
			sender.sendMessage("§cUnbekanntes Team: " + spaced);
		}
		return team;
	}
}
