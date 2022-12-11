/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.stats;

import java.util.List;

import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.stats.api.StatsUtil;

public interface StatsElo {

	public default void insertElo(Stats stats, List<ChatEntry> builder, double elo, long placementElo) {
		StatsUtil.insertElo(stats, builder, elo, placementElo);
	}

}
