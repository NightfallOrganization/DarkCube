/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.scheduler;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;
import static eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;

import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskSchedule;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class HeightDisplayScheduler {
    private final CommonIngame ingame;
    private SchedulerTask task;

    public HeightDisplayScheduler(CommonIngame ingame) {
        this.ingame = ingame;
    }

    public void update(CommonWBUser user) {
        var display = user.heightDisplay();
        if (display.enabled()) {
            var deathHeight = ingame.game().api().mapManager().deathHeight();
            var location = user.location();
            if (location == null) return;
            var currentHeight = location.blockY();
            var diff = Math.max(0, currentHeight - deathHeight);
            if (display.maxDistance() == -1 || display.maxDistance() <= diff) {
                var team = user.team();
                if (team != null && team.canPlay()) {
                    user.sendActionBar(text("» ", DARK_GRAY).append(text(diff, team.nameColor())).append(text(" «", DARK_GRAY)));
                }
            }
        }
    }

    public void start() {
        task = this.ingame.game().scheduler().submit(() -> {
            ingame.game().users().forEach(this::update);
            return TaskSchedule.seconds(1);
        });
    }

    public void stop() {
        task.cancel();
        task = null;
    }
}
