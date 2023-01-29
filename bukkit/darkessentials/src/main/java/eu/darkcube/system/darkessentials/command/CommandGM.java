/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.EssentialCollections;
import eu.darkcube.system.darkessentials.util.GamemodeChanger;

public class CommandGM extends Command {
	public CommandGM() {
		super(DarkEssentials.getInstance(), "gamemode", new Command[0], "Setzt den Gamemode eines Spielers.",
				new Argument[] {
						new Argument("0-3|survival|creative|adventure|spectator",
								"Der Gamemode, in den der Spieler gesetzt werden soll."),
						new Argument("Spieler", "Der Spieler, dessen Gamemode gesetzt werden soll.", false) });
		setAliases("gm", "d_gamemode");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		Set<String> targetNames = new HashSet<>();
		int gamemode = 0;
		if (args.length >= 1) {
			if (args.length == 1 && !(sender instanceof Player)) {
				DarkEssentials.sendMessagePlayernameRequired(sender);
				return true;
			}
			switch (args[0].toLowerCase()) {
			case "survival":
			case "s":
			case "su":
			case "0":
				break;
			case "creative":
			case "c":
			case "1":
				gamemode = 1;
				break;
			case "adventure":
			case "a":
			case "2":
				gamemode = 2;
				break;
			case "spectator":
			case "sp":
			case "3":
				gamemode = 3;
				break;
			default:
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst einen Gamemode angeben!", sender);
				return true;
			}
			args[0] = "%processed%";
			for (String playerName : args) {
				if (!playerName.equals("%processed%")) {
					targetNames.add(playerName);
				}
			}
		}
		GamemodeChanger.changeGamemode(gamemode, sender, targetNames);
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return EssentialCollections.toSortedStringList(
					EssentialCollections.asList(new String[] { "survival", "creative", "adventure", "spectator" }),
					args[0]);
		} else if (args.length > 1) {
			return DarkEssentials.getPlayersStartWith(args);
		}
		return new ArrayList<>();
	}
}

/* Old Implementation */
//@Override
//public boolean execute(CommandSender sender, String[] args) {
//	if (args.length == 0)
//		return false;
//	Set<Player> players = new HashSet<>();
//	Set<String> unresolvedNames = new HashSet<String>();
//	GameMode gm = null;
//	String gmString = null;
//	if (args.length >= 1) {
//		if (args.length == 1 && !(sender instanceof Player)) {
//			Main.sendMessagePlayernameRequired(sender);
//			return true;
//		}
//		switch (args[0].toLowerCase()) {
//		case "survival":
//		case "s":
//		case "su":
//		case "0":
//			gm = GameMode.SURVIVAL;
//			gmString = "Survival";
//			break;
//		case "creative":
//		case "c":
//		case "1":
//			gm = GameMode.CREATIVE;
//			gmString = "Creative";
//			break;
//		case "adventure":
//		case "a":
//		case "2":
//			gm = GameMode.ADVENTURE;
//			gmString = "Adventure";
//			break;
//		case "spectator":
//		case "sp":
//		case "3":
//			gm = GameMode.SPECTATOR;
//			gmString = "Spectator";
//			break;
//		default:
//			Main.getInstance().sendMessage(Main.cFail() + "Du musst einen Gamemode angeben!", sender);
//			return false;
//		}
//		args[0] = "%processed%";
//		for (String playerName : args) {
//			if (!playerName.equals("%processed%")) {
//				if (Bukkit.getPlayer(playerName) != null) {
//					players.add(Bukkit.getPlayer(playerName));
//				} else {
//					unresolvedNames.add(playerName);
//				}
//			}
//		}
//	}
//	if (players.isEmpty() && unresolvedNames.isEmpty()) {
//		if (sender instanceof Player) {
//			players.add((Player) sender);
//		} else {
//			Main.sendMessagePlayernameRequired(sender);
//			return true;
//		}
//	}
//	int count = 0;
//	GameMode senderGM = GameMode.SURVIVAL;
//	if (players.contains(sender)) {
//		Player playerSender = (Player) sender;
//		senderGM = playerSender.getGameMode();
//	}
//	Main.sendMessagePlayerNotFound(unresolvedNames, sender);
//	for (Player current : players) {
//		if (!current.getGameMode().equals(gm)) {
//			current.setGameMode(gm);
//			Main.getInstance().sendMessage(Main.cConfirm() + "Dein Gamemode wurde auf " + Main.cValue() + gmString
//					+ Main.cConfirm() + " geändert.", current);
//		}
//		count++;
//	}
//	if (!(players.size() == 1 && players.contains(sender))) {
//		Main.getInstance()
//				.sendMessage(new StringBuilder().append(Main.cConfirm()).append("Gamemode von ")
//						.append(Main.cValue()).append(count).append(Main.cConfirm()).append(" Spielern auf ")
//						.append(gmString).append(Main.cConfirm()).append(" gesetzt").toString(), sender);
//	} else {
//		if (senderGM.equals(gm)) {
//			Main.getInstance().sendMessage(Main.cFail() + "Du bist bereits in diesem Gamemode!", sender);
//		}
//	}
//	return true;
//}
