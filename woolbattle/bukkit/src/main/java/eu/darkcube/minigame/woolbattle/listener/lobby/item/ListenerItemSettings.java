/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.inventory.SettingsInventory;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class ListenerItemSettings extends Listener<EventInteract> {

	@Override
	@EventHandler
	public void handle(EventInteract e) {
		ItemStack item = e.getItem();
		if (e.getItem().hasItemMeta()) {
			if (ItemManager.getItemId(item).equals(Item.SETTINGS.getItemId())) {
				e.setCancelled(true);
				e.getUser().setOpenInventory(new SettingsInventory(e.getUser()));
			}
		}
	}
}
