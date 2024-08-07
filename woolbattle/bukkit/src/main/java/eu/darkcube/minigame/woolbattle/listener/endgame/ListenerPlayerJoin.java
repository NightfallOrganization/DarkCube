/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.endgame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerPlayerJoin extends Listener<PlayerJoinEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerPlayerJoin(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    @EventHandler
    public void handle(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        WBUser user = WBUser.getUser(p);
        woolbattle.endgame().setPlayerItems(user);
        e.setJoinMessage(null);
        p.setAllowFlight(false);
        p.teleport(woolbattle.lobby().getSpawn());
    }
}
