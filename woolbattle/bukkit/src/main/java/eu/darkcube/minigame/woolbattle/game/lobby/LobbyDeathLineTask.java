/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.game.lobby;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.entity.Player;

public class LobbyDeathLineTask extends Scheduler implements Scheduler.ConfiguredScheduler {
    private final Lobby lobby;

    public LobbyDeathLineTask(Lobby lobby, WoolBattleBukkit woolbattle) {
        super(woolbattle);
        this.lobby = lobby;
    }

    @Override public void run() {
        WBUser.onlineUsers().forEach(u -> {
            if (u.isTrollMode()) return;
            Player p = u.getBukkitEntity();
            if (p.getLocation().getBlockY() < lobby.deathline()) {
                p.teleport(lobby.getSpawn());
            }
        });
    }

    @Override public void start() {
        runTaskTimer(20);
    }

    @Override public void stop() {
        cancel();
    }
}
