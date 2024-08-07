/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerDeathMove extends Listener<PlayerMoveEvent> {

    private final WoolBattleBukkit woolbattle;

    public ListenerDeathMove(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    @EventHandler
    public void handle(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        WBUser user = WBUser.getUser(p);
        if (!user.getTeam().isSpectator()) {
            if (p.getLocation().getY() <= this.woolbattle.gameData().map().deathHeight()) {
                this.woolbattle.ingame().playerUtil().kill(user);
            }
            return;
        }
        if (e.getTo().getY() < 0) {
            p.teleport(user.getTeam().getSpawn());
        }
    }

}
