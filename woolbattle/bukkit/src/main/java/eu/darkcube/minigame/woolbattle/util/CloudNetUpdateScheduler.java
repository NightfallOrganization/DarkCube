/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class CloudNetUpdateScheduler extends Scheduler {
    @Override
    public void run() {
        CloudNetLink.update();
    }
}
