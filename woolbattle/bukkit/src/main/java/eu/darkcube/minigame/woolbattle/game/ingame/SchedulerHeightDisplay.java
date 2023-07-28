/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class SchedulerHeightDisplay extends Scheduler implements ConfiguredScheduler {

    private final WoolBattle woolbattle;

    public SchedulerHeightDisplay(WoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    public static void display(WoolBattle woolbattle, WBUser user) {
        HeightDisplay display = user.heightDisplay();
        if (display.isEnabled()) {
            int deathHeight = woolbattle.gameData().map().deathHeight();
            int currentHeight = user.getBukkitEntity().getLocation().getBlockY();
            int diff = (diff = currentHeight - deathHeight) < 0 ? 0 : diff;

            if (display.maxDistance == -1 || display.maxDistance < diff) {
                Team team = user.getTeam();
                if (team != null) {
                    if (team.getType() != TeamType.SPECTATOR) {
                        user.user().sendActionBar(LegacyComponentSerializer.legacySection()
                                .deserialize(
                                        "§8» " + display.getColor().toString() + diff + " §8«"));
                    }
                }
            }
        }
    }

    public void display(WBUser user) {
        display(woolbattle, user);
    }

    @Override
    public void start() {
        this.runTaskTimer(20);
    }

    @Override
    public void stop() {
        cancel();
    }

    @Override
    public void run() {
        WBUser.onlineUsers().forEach(this::display);
    }
}
