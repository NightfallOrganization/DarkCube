/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.endgame;

import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {

    @Override
    @EventHandler
    public void handle(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        var user = WBUser.getUser(e.getPlayer());
        user.setTeam(null);
    }
}
