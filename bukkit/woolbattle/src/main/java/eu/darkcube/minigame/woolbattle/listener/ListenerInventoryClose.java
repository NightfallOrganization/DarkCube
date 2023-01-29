/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerInventoryClose extends Listener<InventoryCloseEvent>{
	@Override
	@EventHandler
	public void handle(InventoryCloseEvent e) {
		User user = WoolBattle.getInstance().getUserWrapper().getUser(e.getPlayer().getUniqueId());
		user.setOpenInventory(null);
	}
}
