/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ListenerInventoryClose extends Listener<InventoryCloseEvent> {
    @Override
    @EventHandler
    public void handle(InventoryCloseEvent e) {
        WBUser user = WBUser.getUser((Player) e.getPlayer());
        user.setOpenInventory(null);
    }
}
