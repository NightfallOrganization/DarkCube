/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.EssentialCollections;
import eu.darkcube.system.darkessentials.util.ExpFix;

public class CommandXp extends Command {

	public CommandXp() {
		super(Main.getInstance(), "experience", new Command[0],
				"Verändert die Erfahrungspunkte eines Spielers oder ruft diese ab.",
				new Argument[] {
						new Argument("add|subtract|set|get",
								"Wie die EP verändert werden oder ob sie abgerufen werden."),
						new Argument("Wert|Wert+L", "Anzehl der EP bzw Level, die addiert/subtrahiert/gesetzt werden.",
								false),
						new Argument("Spieler", "Der Spieler, dessen EP verändert/abgerufen werden.", false) });
		setAliases("d_experience", "xp");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		int count = 0;
		int value = 0;
		boolean modifyLevels = false;
		String action = "set";
		Set<Player> players = new HashSet<>();
		Set<String> unresolvedNames = new HashSet<>();
		if (args.length == 1 && sender instanceof Player) {
			if (args[0].equalsIgnoreCase("get")) {
				Main.getInstance().sendMessage(new StringBuilder().append(Main.cConfirm()).append("Du hast aktuell ")
						.append(Main.cValue()).append(ExpFix.getTotalExperience((Player) sender))
						.append(Main.cConfirm()).append(" Erfahrungspunkte. (").append(Main.cValue())
						.append(((Player) sender).getLevel()).append(Main.cConfirm()).append(" Level").toString(),
						sender);
				return true;
			}
			return false;
		} else if (args.length > 1) {
			try {
				switch (args[0].toLowerCase()) {
				case "get":
					args[0] = "%processed%";
					for (String playerName : args) {
						if (!playerName.equals("%processed%")) {
							if (Bukkit.getPlayer(playerName) != null) {
								Player current = Bukkit.getPlayer(playerName);
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append("§aDer Spieler §5").append(current.getName())
										.append(" §ahat aktuell §5").append(ExpFix.getTotalExperience(current))
										.append(" §aErfahrungspunkte (§5").append(current.getLevel())
										.append(" §aLevel)");
								Main.getInstance().sendMessage(stringBuilder.toString(), sender);
							} else {
								unresolvedNames.add(playerName);
							}
						}
					}
					Main.sendMessagePlayerNotFound(unresolvedNames, sender);
					return true;
				case "set":
				case "add":
				case "subtract":
					if (args.length > 1) {
						if ((args[1].charAt(args[1].length() - 1) == 'l' || args[1].charAt(args[1].length() - 1) == 'L')
								&& args[1].length() != 0) {
							try {
								value = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
								args[1] = "%processed%";
								modifyLevels = true;
							} catch (Exception e) {
								Main.getInstance().sendMessage(
										Main.cFail() + "Du musst eine Zahl oder eine Zalhl+L §cangeben!", sender);
								return true;
							}
						} else {
							try {
								value = Integer.parseInt(args[1]);
								args[1] = "%processed%";
							} catch (Exception e) {
								Main.getInstance().sendMessage(
										Main.cFail() + "Du musst eine Zahl oder eine Zalhl+L §cangeben!", sender);
								return true;
							}
						}
						action = args[0];
						args[0] = "%processed%";
						for (String playerName : args) {
							if (!playerName.equals("%processed%")) {
								if (Bukkit.getPlayer(playerName) != null) {
									players.add(Bukkit.getPlayer(playerName));
								} else {
									unresolvedNames.add(playerName);
								}
							}
						}
					} else {
						Main.getInstance().sendMessage(Main.cFail() + "Du musst eine Zahl oder eine Zalhl+L §cangeben!",
								sender);
						return true;
					}
					break;
				default:
					throw new IllegalArgumentException();
				}
			} catch (Exception e) {
				Main.getInstance().sendMessage(Main.cFail() + "Du musst eine Aktion angeben!", sender);
				return true;
			}

		} else {
			return false;
		}
		if (players.isEmpty() && unresolvedNames.isEmpty()) {
			if (sender instanceof Player) {
				players.add((Player) sender);
			} else {
				Main.sendMessagePlayernameRequired(sender);
				return true;
			}

		}
		Main.sendMessagePlayerNotFound(unresolvedNames, sender);
		if (action.equalsIgnoreCase("set")) {
			for (Player current : players) {
				if (modifyLevels) {
					current.setLevel(value);
					Main.getInstance()
							.sendMessage(new StringBuilder().append(Main.cConfirm()).append("Deine Level wurden auf ")
									.append(Main.cValue()).append(value).append(Main.cConfirm()).append(" gesetzt.")
									.toString(), current);
				} else {
					ExpFix.setTotalExperience(current, value);
					Main.getInstance().sendMessage(new StringBuilder().append(Main.cConfirm())
							.append("Deine Erfahrungspunkte wurden auf ").append(Main.cValue()).append(value)
							.append(Main.cConfirm()).append(" gesetzt.").toString(), current);
				}
				count++;
			}
		} else {
			for (Player current : players) {
				if (modifyLevels) {
					if (action.equalsIgnoreCase("add")) {
						current.setLevel(current.getLevel() + value);
						Main.getInstance().sendMessage(
								new StringBuilder().append(Main.cConfirm()).append("Du hast ").append(Main.cValue())
										.append(value).append(Main.cConfirm()).append(" Level erhalten.").toString(),
								current);
					} else {
						current.setLevel(current.getLevel() - value);
						Main.getInstance().sendMessage(
								new StringBuilder().append(Main.cConfirm()).append("Du hast ").append(Main.cValue())
										.append(value).append(Main.cConfirm()).append(" Level verloren.").toString(),
								current);
					}
				} else {
					if (action.equalsIgnoreCase("add")) {
						ExpFix.setTotalExperience(current, ExpFix.getTotalExperience(current) + value);
						Main.getInstance()
								.sendMessage(new StringBuilder().append(Main.cConfirm()).append("Du hast ")
										.append(Main.cValue()).append(value).append(Main.cConfirm())
										.append(" Erfahrungspunkte erhalten.").toString(), current);
					} else {
						try {
							ExpFix.setTotalExperience(current, ExpFix.getTotalExperience(current) - value);
						} catch (Exception e) {
							ExpFix.setTotalExperience(current, 0);
						}
						Main.getInstance()
								.sendMessage(new StringBuilder().append(Main.cConfirm()).append("Du hast ")
										.append(Main.cValue()).append(value).append(Main.cConfirm())
										.append(" Erfahrungspunkte verloren.").toString(), current);

					}

				}
				count++;
			}
		}
		if (!(players.size() == 1 && players.contains(sender)))
			Main.getInstance()
					.sendMessage(new StringBuilder().append(Main.cConfirm()).append("Erfahrungspunkte von ")
							.append(Main.cValue()).append(count).append(Main.cConfirm()).append(" Spielern verändert!")
							.toString(), sender);
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return EssentialCollections.toSortedStringList(EssentialCollections.asList("get", "set", "add", "subtract"),
					args[0]);
		}
		if (args.length > 2 || (args.length == 2 && args[0].equalsIgnoreCase("get"))) {
			return Main.getPlayersStartWith(args);
		}
		return new ArrayList<>();
	}
}
