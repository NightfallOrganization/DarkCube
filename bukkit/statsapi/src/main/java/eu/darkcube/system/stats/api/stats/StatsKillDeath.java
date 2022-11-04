package eu.darkcube.system.stats.api.stats;

import java.util.List;

import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.stats.api.StatsUtil;

public interface StatsKillDeath {

	public default void insertKillDeath(Stats stats, List<ChatEntry> builder, long kills, long deaths, double ratio,
			long placementKills, long placementDeaths, long placementRatio) {
		StatsUtil.insertKills(stats, builder, kills, placementKills);
		StatsUtil.insertDeaths(stats, builder, deaths, placementDeaths);
		StatsUtil.insertKillDeathRatio(stats, builder, ratio, placementRatio);
	}
}
