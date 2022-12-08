/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.InventoryBuilder;
import eu.darkcube.minigame.woolbattle.util.InventoryId;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemBuilder;
import eu.darkcube.minigame.woolbattle.util.ItemManager;

public class ListenerInteract extends Listener<PlayerInteractEvent> {

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (user.getTeam().getType() != TeamType.SPECTATOR) {
			return;
		}
		e.setCancelled(true);
		if (e.getItem() != null && e.getItem().getType() != Material.AIR
				&& ItemManager.getItemId(e.getItem()).equals(Item.TELEPORT_COMPASS.getItemId())) {
			p.openInventory(this.createInventory(user));
			user.setOpenInventory(InventoryId.COMPASS_TELEPORT);
		}
	}

	@SuppressWarnings("deprecation")
	private Inventory createInventory(User user) {
		InventoryBuilder builder = new InventoryBuilder(Message.INVENTORY_COMPASS.getMessage(user));
		int slot = 0;
		for (User u : WoolBattle.getInstance().getUserWrapper().getUsers()) {
			if (u.getTeam().getType() == TeamType.SPECTATOR) {
				continue;
			}
			ItemBuilder b = new ItemBuilder(Material.SKULL_ITEM).setOwner(u.getPlayerName())
					.setDisplayName(u.getTeamPlayerName()).setDurability((short) 3);
			builder.setItem(slot++, b.build());
		}
		return builder.build();
	}
}
