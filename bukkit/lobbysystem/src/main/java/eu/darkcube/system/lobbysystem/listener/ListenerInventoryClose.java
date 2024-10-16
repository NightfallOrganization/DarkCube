/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.potion.PotionEffectType;
import eu.darkcube.system.lobbysystem.inventory.InventoryPlayer;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;

public class ListenerInventoryClose extends BaseListener {

	@EventHandler
	public void handle(InventoryCloseEvent e) {
		e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
		LobbyUser user =
				UserWrapper.fromUser(UserAPI.getInstance().getUser(e.getPlayer().getUniqueId()));
		user.setOpenInventory(new InventoryPlayer());
		// InventoryPServer.PAGE.remove(user);
	}
}
