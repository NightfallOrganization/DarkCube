/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby;

import eu.darkcube.minigame.woolbattle.listener.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ListenerPlayerDropItem extends Listener<PlayerDropItemEvent> {

    @Override
    @EventHandler
    public void handle(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }
}
