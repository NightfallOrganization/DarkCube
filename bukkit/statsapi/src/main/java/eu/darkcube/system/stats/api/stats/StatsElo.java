/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.stats;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.stats.api.StatsUtil;

public interface StatsElo {

    default Component insertElo(Stats stats, double elo, long placementElo) {
        return StatsUtil.insertElo(stats, elo, placementElo);
    }

}
