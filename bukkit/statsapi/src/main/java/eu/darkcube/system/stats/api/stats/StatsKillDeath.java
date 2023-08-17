/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.stats;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.stats.api.StatsUtil;

public interface StatsKillDeath {

    default Component insertKillDeath(Stats stats, long kills, long deaths, double ratio, long placementKills, long placementDeaths, long placementRatio) {
        return StatsUtil
                .insertKills(stats, kills, placementKills)
                .append(StatsUtil.insertDeaths(stats, deaths, placementDeaths))
                .append(StatsUtil.insertKillDeathRatio(stats, ratio, placementRatio));
    }
}
