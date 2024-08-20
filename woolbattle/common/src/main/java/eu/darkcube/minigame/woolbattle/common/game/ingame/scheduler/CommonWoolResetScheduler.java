/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.scheduler;

import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskSchedule;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;

public class CommonWoolResetScheduler {

    private final CommonIngame ingame;
    private SchedulerTask task;

    public CommonWoolResetScheduler(CommonIngame ingame) {
        this.ingame = ingame;
    }

    public void start() {
        task = this.ingame.game().scheduler().submit(() -> {
            var world = ingame.world();
            if (world == null) {
                ingame.game().api().woolbattle().logger().error("World to regenerate wool in was null - stopping scheduler");
                return TaskSchedule.stop();
            }
            for (var entry : world.brokenWool().entrySet()) {
                var block = entry.getKey();
                var wool = entry.getValue();
                wool.apply(block);
            }
            world.brokenWool().clear();
            return TaskSchedule.tick(16);
        });
    }

    public void stop() {
        task.cancel();
        task = null;
    }
}
