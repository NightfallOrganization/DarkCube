/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.stats.api.command.CommandStatsTop;
import eu.darkcube.system.stats.api.stats.Stats;

public class StatsUtil {

	public static Component insertWinLossRatio(Stats stats, double ratio, long placement) {
		return StatsUtil.insert(stats, "Winrate (%)",
				String.format("%.2f", ratio).replace(',', '.'), placement, "wl");
	}

	public static Component insertWins(Stats stats, long wins, long placement) {
		return StatsUtil.insert(stats, "Siege", Long.toString(wins), placement, "wins");
	}

	public static Component insertLosses(Stats stats, long losses, long placement) {
		return StatsUtil.insert(stats, "Niederlagen", Long.toString(losses), placement, "losses");
	}

	public static Component insertKillDeathRatio(Stats stats, double ratio, long placement) {
		return StatsUtil.insert(stats, "K/D", String.format("%.2f", ratio).replace(',', '.'),
				placement, "kd");
	}

	public static Component insertKills(Stats stats, long kills, long placement) {
		return StatsUtil.insert(stats, "Kills", Long.toString(kills), placement, "kills");
	}

	public static Component insertDeaths(Stats stats, long deaths, long placement) {
		return StatsUtil.insert(stats, "Tode", Long.toString(deaths), placement, "deaths");
	}

	public static Component insertElo(Stats stats, double elo, long placement) {
		return StatsUtil.insert(stats, "§oElo", String.format("%.0f", elo), placement, "elo");
	}

	public static Component insert(Stats stats, String key, String value, long placement,
			String mysqlColumn) {
		//		builder.append(stats.format(key, value, placement));
		return Stats.format(key, value, placement).appendNewline().hoverEvent(HoverEvent.showText(
						Component.text("Klicke um die Top-Liste anzuzeigen!").color(NamedTextColor.GRAY)))
				.clickEvent(ClickEvent.runCommand(
						"/" + CommandStatsTop.INSTANCE.getName() + " " + stats.getGamemode()
								.toString().toLowerCase() + " " + mysqlColumn + " "
								+ stats.getDuration().toString().toLowerCase()));
	}

}
