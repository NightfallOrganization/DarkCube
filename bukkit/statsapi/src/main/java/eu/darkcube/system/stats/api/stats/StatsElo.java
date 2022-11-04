package eu.darkcube.system.stats.api.stats;

import java.util.List;

import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.stats.api.StatsUtil;

public interface StatsElo {

	public default void insertElo(Stats stats, List<ChatEntry> builder, double elo, long placementElo) {
		StatsUtil.insertElo(stats, builder, elo, placementElo);
	}

}
