/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class ListenerBorder extends BaseListener {

    private Map<Player, Integer> failes = new HashMap<>();

    @EventHandler public void handle(PlayerMoveEvent e) {
        if (!UserWrapper.fromUser(UserAPI.instance().user(e.getPlayer().getUniqueId())).isBuildMode() && Lobby
                .getInstance()
                .getDataManager()
                .getBorder()
                .isOutside(e.getPlayer())) {
            failes.put(e.getPlayer(), failes.getOrDefault(e.getPlayer(), 0) + 1);
            if (failes.get(e.getPlayer()) > 10) {
                e.getPlayer().teleport(Lobby.getInstance().getDataManager().getSpawn());
            } else {
                Lobby.getInstance().getDataManager().getBorder().boost(e.getPlayer());
            }
        } else {
            failes.remove(e.getPlayer());
        }
    }

    @EventHandler public void handle(PlayerQuitEvent e) {
        failes.remove(e.getPlayer());
    }
}
