/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ListenerBlock extends BaseListener {

    @EventHandler public void handle(BlockPlaceEvent e) {
        LobbyUser user = UserWrapper.fromUser(UserAPI.instance().user(e.getPlayer().getUniqueId()));
        if (!user.isBuildMode()) {
            e.setBuild(false);
            e.setCancelled(true);
        }
    }

    @EventHandler public void handle(BlockBreakEvent e) {
        LobbyUser user = UserWrapper.fromUser(UserAPI.instance().user(e.getPlayer().getUniqueId()));
        e.setExpToDrop(0);
        if (!user.isBuildMode()) {
            e.setCancelled(true);
        }
    }
}
