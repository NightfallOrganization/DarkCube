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

public class PerkCooldownScheduler {
    private final CommonIngame ingame;
    private SchedulerTask task;

    public PerkCooldownScheduler(CommonIngame ingame) {
        this.ingame = ingame;
    }

    public void start() {
        task = this.ingame.game().scheduler().submit(() -> {
            for (var user : ingame.game().users()) {
                for (var perk : user.perks().perks()) {
                    if (perk.perk().autoCountdownCooldown()) {
                        var cooldown = perk.cooldown();
                        if (cooldown > 0) {
                            perk.cooldown(cooldown - 1);
                        }
                    }
                }
            }
            return TaskSchedule.nextTick();
        });
    }

    public void stop() {
        task.cancel();
        task = null;
    }
}
