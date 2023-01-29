/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.ColorUtil;
import eu.darkcube.system.commandapi.Command;

public class CommandCreateTeam extends Command {

	public CommandCreateTeam() {
		super(WoolBattle.getInstance(), "createTeam", new Command[0], "Erstellt ein Team", CommandArgument.TEAM,
				CommandArgument.WEIGHT, CommandArgument.WOOL_COLOR, CommandArgument.NAME_COLOR,
				CommandArgument.MAX_PLAYERS);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		switch (args.length) {
		case 1:
			return Arrays.toSortedStringList(
					Arrays.asList("RED", "BLUE", "BLACK", "GRAY", "GREEN", "WHITE", "PURPLE", "YELLOW").stream().filter(id -> {
						for (TeamType t : TeamType.values()) {
							if (t.getDisplayNameKey() == id) {
								return false;
							}
						}
						return true;
					}).collect(Collectors.toList()), args[0]);
		case 2:
			return Arrays.toSortedStringList(
					Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).stream().filter(id -> {
						for (TeamType t : TeamType.values()) {
							if (t.getWeight() == id) {
								return false;
							}
						}
						return true;
					}).collect(Collectors.toList()), args[1]);
		case 3:
			return Arrays.toSortedStringList(DyeColor.values(), args[2].toLowerCase());
		case 4:
			return Arrays.toSortedStringList(ChatColor.values(), args[3].toLowerCase());
		case 5:
			return Arrays.toSortedStringList(Arrays.asList(1, 2, 4, 6, 8, 40), args[4]);
		default:
			return super.onTabComplete(args);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 5) {
			TeamType type = TeamType.byDisplayNameKey(args[0]);
			if (type != null && !type.isDeleted()) {
				sender.sendMessage("§cEs existiert bereits ein Team mit dem Namen '" + type.getDisplayNameKey() + "'!");
				return true;
			}
			Integer weight = null;
			try {
				weight = Integer.valueOf(args[1]);
				if (weight < 0)
					weight = null;
			} catch (Exception ex) {
			}
			if (weight == null) {
				sender.sendMessage("§cBitte gib als Sortierung eine positive Zahl an!");
				return true;
			}
			for (TeamType t : TeamType.values()) {
				if (t.getWeight() == weight) {
					sender.sendMessage(
							"§cDiese Sortierung ist bereits dem Team " + t.getDisplayNameKey() + " vergeben!");
					return true;
				}
			}

			DyeColor woolColor = DyeColor.getByData(ColorUtil.byDyeColor(args[2]));
			if (woolColor == null) {
				sender.sendMessage("§cBitte gib eine gültige Wollfarbe an!");
				return true;
			}
			ChatColor chatColor = ChatColor.getByChar(ColorUtil.byChatColor(args[3]));
			if (chatColor == null) {
				sender.sendMessage("§cBitte gib eine gültige Namenfarbe an!");
				return true;
			}
			Integer maxPlayers = null;
			try {
				maxPlayers = Integer.valueOf(args[4]);
				if (maxPlayers <= 0)
					maxPlayers = null;
			} catch (Exception ex) {
			}
			if (maxPlayers == null) {
				sender.sendMessage("§cBitte gib als Maximale Spieleranzahl eine positive Zahl an!");
				return true;
			}
			type = new TeamType(args[0], weight, ColorUtil.byDyeColor(woolColor), chatColor, false, maxPlayers);
			type.save();
			sender.sendMessage("§7Du hast ein neues Team mit dem Namen '" + args[0] + "', der Sortierung " + weight
					+ ", der Wollfarbe " + woolColor.name() + ", der Namenfarbe " + chatColor.name()
					+ " und der Maximalen Spieleranzahl " + maxPlayers
					+ " erstellt!\nBedenke, dass du den Server neustarten musst damit das Team erkannt wird!");
			return true;
		}
		return false;
	}
}
