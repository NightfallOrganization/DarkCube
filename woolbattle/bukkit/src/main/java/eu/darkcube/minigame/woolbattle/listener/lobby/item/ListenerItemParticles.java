/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class ListenerItemParticles extends Listener<EventInteract> {

	@Override
	@EventHandler
	public void handle(EventInteract e) {
		ItemStack item = e.getItem();
		if (item.hasItemMeta()) {
			String itemId = ItemManager.getItemId(item);
			Player p = e.getPlayer();
			WBUser user = WBUser.getUser(p);
			if (itemId.equals(Item.LOBBY_PARTICLES_ON.getItemId())) {
				e.setCancelled(true);
				user.particles(false);
				e.setItem(Item.LOBBY_PARTICLES_OFF.getItem(user));
			} else if (itemId.equals(Item.LOBBY_PARTICLES_OFF.getItemId())) {
				e.setCancelled(true);
				user.particles(true);
				e.setItem(Item.LOBBY_PARTICLES_ON.getItem(user));
			}
		}
	}
}
