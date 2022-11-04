package eu.darkcube.system.stats.api;

import java.util.List;

import eu.darkcube.system.ChatUtils;
import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.stats.api.command.CommandStatsTop;
import eu.darkcube.system.stats.api.stats.Stats;

public class StatsUtil {

	public static void insertWinLossRatio(Stats stats, List<ChatEntry> builder, double ratio, long placement) {
		insert(stats, builder, "Winrate (%)", String.format("%.2f", ratio).replace(',', '.'), placement, "wl");
	}

	public static void insertWins(Stats stats, List<ChatEntry> builder, long wins, long placement) {
		insert(stats, builder, "Siege", Long.toString(wins), placement, "wins");
	}

	public static void insertLosses(Stats stats, List<ChatEntry> builder, long losses, long placement) {
		insert(stats, builder, "Niederlagen", Long.toString(losses), placement, "losses");
	}

	public static void insertKillDeathRatio(Stats stats, List<ChatEntry> builder, double ratio, long placement) {
		insert(stats, builder, "K/D", String.format("%.2f", ratio).replace(',', '.'), placement, "kd");
	}

	public static void insertKills(Stats stats, List<ChatEntry> builder, long kills, long placement) {
		insert(stats, builder, "Kills", Long.toString(kills), placement, "kills");
	}

	public static void insertDeaths(Stats stats, List<ChatEntry> builder, long deaths, long placement) {
		insert(stats, builder, "Tode", Long.toString(deaths), placement, "deaths");
	}

	public static void insertElo(Stats stats, List<ChatEntry> builder, double elo, long placement) {
		insert(stats, builder, "§oElo", String.format("%.0f", elo), placement, "elo");
	}

	public static void insert(Stats stats, List<ChatEntry> builder, String key, String value, long placement,
			String mysqlColumn) {
//		builder.append(stats.format(key, value, placement));
		ChatUtils.ChatEntry.Builder cbuilder = new ChatUtils.ChatEntry.Builder();
		cbuilder.text(Stats.format(key, value, placement));
		cbuilder.hover("§7Klicke um die Top-Liste anzuzeigen!");
		cbuilder.click("/" + CommandStatsTop.INSTANCE.getName() + " " + stats.getGamemode().toString().toLowerCase()
				+ " " + mysqlColumn + " " + stats.getDuration().toString().toLowerCase());
		builder.addAll(Arrays.asList(cbuilder.build()));
	}
}
