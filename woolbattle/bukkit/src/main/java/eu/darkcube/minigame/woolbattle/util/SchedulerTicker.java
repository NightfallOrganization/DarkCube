/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerTask;
import org.bukkit.scheduler.BukkitRunnable;

public class SchedulerTicker extends BukkitRunnable {
    private final WoolBattleBukkit woolbattle;

    public SchedulerTicker(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public void run() {
        for (SchedulerTask scheduler : woolbattle.schedulers()) {
            if (scheduler.canExecute()) scheduler.run();
        }
    }
}
