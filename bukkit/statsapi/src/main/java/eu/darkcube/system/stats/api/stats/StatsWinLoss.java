/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.stats;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.stats.api.StatsUtil;

public interface StatsWinLoss {

    default Component insertWinLoss(Stats stats, long wins, long losses, double ratio, long placementWins, long placementLosses, long placementRatio) {
        return StatsUtil
                .insertWins(stats, wins, placementWins)
                .append(StatsUtil.insertLosses(stats, losses, placementLosses))
                .append(StatsUtil.insertWinLossRatio(stats, ratio, placementRatio));
    }
}
