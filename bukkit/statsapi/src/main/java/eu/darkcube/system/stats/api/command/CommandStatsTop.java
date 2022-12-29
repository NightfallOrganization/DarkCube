/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.command;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.stats.api.Arrays;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.StatsPlugin;
import eu.darkcube.system.stats.api.gamemode.GameMode;
import eu.darkcube.system.stats.api.mysql.MySQL;
import eu.darkcube.system.stats.api.mysql.Result;
import eu.darkcube.system.stats.api.stats.Stats;
import eu.darkcube.system.stats.api.user.StatsUserManager;
import eu.darkcube.system.util.AdventureSupport;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandStatsTop extends Command {

	public static final CommandStatsTop INSTANCE = new CommandStatsTop();
	public static final Map<GameMode, String[]> availableCategories = new HashMap<>();
	private static final String[] availableCategoriesWoolbattle =
			{"elo", "kills", "deaths", "wins", "losses", "kd", "wl"};
	private static final Map<String, String> converts = new HashMap<>();

	static {
		availableCategories.put(GameMode.WOOLBATTLE, availableCategoriesWoolbattle);

		converts.put("wl", "winLossRatio");
		converts.put("kd", "killDeathRatio");
	}

	private CommandStatsTop() {
		super(StatsPlugin.getInstance(), "statstop", new Command[0], "Ruft die Top-Stats ab",
				new Argument("Spielmodus", "Der Spielmodus"),
				new Argument("Kategorie", "Die Kategorie"),
				new Argument("Duration", "Die Duration", false));
	}

	public static String convertCategoryToMysql(String cat) {
		for (String s : converts.keySet()) {
			if (s.equals(cat)) {
				return converts.get(s);
			}
		}
		return cat;
	}

	public static String convertCategoryFromMysql(String cat) {
		for (String k : converts.keySet()) {
			String s = converts.get(k);
			if (s.equals(cat)) {
				return k;
			}
		}
		return cat;
	}

	private static String getCategory(GameMode gamemode, String category) {
		String[] a = availableCategories.get(gamemode);
		for (String s : a) {
			if (s.equalsIgnoreCase(category)) {
				return s;
			}
		}
		return null;
	}

	public static Component getTopPlayers(String tableBaseName, String category, int playerCount,
			String command, Duration duration) {
		Result result = MySQL.mysql.getResult(
				"SELECT uuid, " + category + " FROM " + MySQL.getTableName(tableBaseName, duration)
						+ " ORDER BY " + category + " DESC LIMIT " + playerCount);
		result.goToColumn(1);
		ResultSet res = result.raw();

		UUID[] uuids = new UUID[playerCount];
		String[] values = new String[playerCount];
		try {
			res.beforeFirst();
			for (int i = 0; i < playerCount; i++) {
				if (!res.next()) {
					break;
				}
				uuids[i] = UUID.fromString(res.getString(1));
				values[i] = res.getString(2);
			}
			res.first();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		boolean decimalValues = false;
		for (String value : values) {
			if (value == null)
				continue;
			if (value.contains(".")) {
				decimalValues = true;
				break;
			}
		}
		Component component = Component.text("");
		for (int i = 0; i < uuids.length; i++) {
			UUID uuid = uuids[i];
			String value = values[i];
			if (uuid == null || value == null) {
				continue;
			}
			if (decimalValues) {
				try {
					value = String.format("%.2f", Double.valueOf(value));
				} catch (Exception ignored) {
				}
				//				String[] split = value.split("\\.");
				//				if (split.length >= 2) {
				//					if (split[1].length() > 2) {
				//						split[1] = split[1].substring(0, 2);
				//					}
				//					value = split[0] + "." + split[1];
				//				}
			}

			String username = StatsUserManager.getOfflineUser(uuid).getName();
			String cmd = command.replace("%name%", username);
			component = component.append(Stats.formatToplist(username, i + 1, value)
					.clickEvent(ClickEvent.runCommand(cmd)).hoverEvent(
							Component.text("Klicke um die Statistiken von ")
									.color(NamedTextColor.GRAY)
									.append(Component.text(username).color(NamedTextColor.GOLD))
									.append(Component.text(" anzuzeigen")
											.color(NamedTextColor.GRAY)))).appendNewline();
		}
		return component;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(new String[] {"woolbattle"}, args[0].toLowerCase());
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("woolbattle")) {
				return Arrays.toSortedStringList(availableCategoriesWoolbattle,
						args[1].toLowerCase());
			}
		} else if (args.length == 3) {
			return Arrays.toSortedStringList(Duration.values(), args[2].toUpperCase());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 2 || args.length == 3) {
			String gamemodeString = args[0];
			GameMode gamemode = null;
			if (gamemodeString.equalsIgnoreCase("woolbattle")) {
				gamemode = GameMode.WOOLBATTLE;
			}
			if (gamemode == null) {
				sender.sendMessage("§cUngültiger Spielmodus '§6" + gamemodeString + "§7'!");
				return true;
			}

			String categoryString = args[1];
			String category = getCategory(gamemode, categoryString);

			if (category == null) {
				sender.sendMessage("§cUngültige Kategorie '§6" + categoryString + "§7'!");
				return true;
			}

			Duration duration = Duration.ALLTIME;
			if (args.length == 3) {
				try {
					duration = Duration.valueOf(args[2].toUpperCase());
				} catch (Exception ex) {
					sender.sendMessage("§cUngültige Zeitspanne '§6" + args[2] + "§7'!");
					return true;
				}
			}
			AdventureSupport.audienceProvider().sender(sender).sendMessage(
					getTopPlayers(MySQL.WOOLBATTLE_BASE_NAME, convertCategoryToMysql(category), 10,
							"/stats %name% " + duration, duration));
			return true;
		}
		sender.sendMessage(getSimpleLongUsage());
		return true;
	}
}
