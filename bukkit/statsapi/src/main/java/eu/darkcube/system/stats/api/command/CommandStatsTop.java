package eu.darkcube.system.stats.api.command;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.ChatBaseComponent;
import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.ChatUtils.ChatEntry.Builder;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.stats.api.Arrays;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.StatsPlugin;
import eu.darkcube.system.stats.api.gamemode.GameMode;
import eu.darkcube.system.stats.api.mysql.MySQL;
import eu.darkcube.system.stats.api.mysql.Result;
import eu.darkcube.system.stats.api.stats.Stats;
import eu.darkcube.system.stats.api.user.StatsUserManager;

public class CommandStatsTop extends Command {

	public static final CommandStatsTop INSTANCE = new CommandStatsTop();

	private static final String[] availableCategoriesWoolbattle = {
			"elo", "kills", "deaths", "wins", "losses", "kd", "wl"
	};
	private static final Map<String, String> converts = new HashMap<>();

	public static final Map<GameMode, String[]> availableCategories = new HashMap<>();
	static {
		availableCategories.put(GameMode.WOOLBATTLE, availableCategoriesWoolbattle);

		converts.put("wl", "winLossRatio");
		converts.put("kd", "killDeathRatio");
	}

	private CommandStatsTop() {
		super(StatsPlugin.getInstance(), "statstop", new Command[0], "Ruft die Top-Stats ab",
				new Argument("Spielmodus", "Der Spielmodus"), new Argument("Kategorie", "Die Kategorie"),
				new Argument("Duration", "Die Duration", false));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(new String[] {
					"woolbattle"
			}, args[0].toLowerCase());
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("woolbattle")) {
				return Arrays.toSortedStringList(availableCategoriesWoolbattle, args[1].toLowerCase());
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

			ChatBaseComponent chat = getTopPlayers(MySQL.WOOLBATTLE_BASE_NAME, convertCategoryToMysql(category), 10,
					"/stats %name% " + duration.toString(), duration);
			if (sender instanceof Player) {
				chat.sendPlayer((Player) sender);
			} else {
				try {
					Method m = chat.getComponent().getClass().getMethod("getText");
					sender.sendMessage(m.invoke(chat.getComponent()).toString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return true;
		}
		sender.sendMessage(getSimpleLongUsage());
		return true;
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

	public static ChatBaseComponent getTopPlayers(String tableBaseName, String category, int playerCount,
			String command, Duration duration) {
		Result result = MySQL.mysql.getResult("SELECT uuid, " + category + " FROM "
				+ MySQL.getTableName(tableBaseName, duration) + " ORDER BY " + category + " DESC LIMIT " + playerCount);
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

		List<ChatEntry> entries = new ArrayList<>();
		for (int i = 0; i < uuids.length; i++) {
			UUID uuid = uuids[i];
			String value = values[i];
			if (uuid == null || value == null) {
				continue;
			}
			if (decimalValues) {
				try {
					value = String.format("%.2f", Double.valueOf(value));
				} catch (Exception e) {
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
			Builder builder = new Builder();
			builder.text(Stats.formatToplist(username, i + 1, value) + "\n");
			builder.click(cmd);
			builder.hover("§7Klicke um die Statistiken von §6" + username + " §7anzuzeigen");
			entries.addAll(Arrays.asList(builder.build()));
		}
		ChatBaseComponent component = ChatEntry.buildArray(entries.toArray(new ChatEntry[0]));
		return component;
	}
}
