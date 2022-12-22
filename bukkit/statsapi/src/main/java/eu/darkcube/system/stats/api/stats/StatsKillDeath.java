/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.stats;

import java.util.List;

import eu.darkcube.system.util.ChatUtils.ChatEntry;
import eu.darkcube.system.stats.api.StatsUtil;

public interface StatsKillDeath {

	public default void insertKillDeath(Stats stats, List<ChatEntry> builder, long kills, long deaths, double ratio,
			long placementKills, long placementDeaths, long placementRatio) {
		StatsUtil.insertKills(stats, builder, kills, placementKills);
		StatsUtil.insertDeaths(stats, builder, deaths, placementDeaths);
		StatsUtil.insertKillDeathRatio(stats, builder, ratio, placementRatio);
	}
}
