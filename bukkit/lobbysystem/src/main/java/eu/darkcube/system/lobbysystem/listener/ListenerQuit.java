/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerQuit extends BaseListener {

    @EventHandler public void handle(PlayerQuitEvent e) {
        LobbyUser u = UserWrapper.fromUser(UserAPI.instance().user(e.getPlayer().getUniqueId()));
        u.stopJaR();
        Lobby.getInstance().savePlayer(u);
    }
}
