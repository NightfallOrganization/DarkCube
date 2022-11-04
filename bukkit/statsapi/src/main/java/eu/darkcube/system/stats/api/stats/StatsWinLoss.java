package eu.darkcube.system.stats.api.stats;

import java.util.List;

import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.stats.api.StatsUtil;

public interface StatsWinLoss {

	public default void insertWinLoss(Stats stats, List<ChatEntry> builder, long wins, long losses,
			double ratio, long placementWins, long placementLosses, long placementRatio) {
		StatsUtil.insertWins(stats, builder, wins, placementWins);
		StatsUtil.insertLosses(stats, builder, losses, placementLosses);
		StatsUtil.insertWinLossRatio(stats, builder, ratio, placementRatio);
	}
}
