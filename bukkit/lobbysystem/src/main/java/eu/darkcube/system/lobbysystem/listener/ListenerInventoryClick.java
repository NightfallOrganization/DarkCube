/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryConfirm;
import eu.darkcube.system.lobbysystem.inventory.InventorySettings;
import eu.darkcube.system.lobbysystem.inventory.InventoryWoolBattle;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServer;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerOwn;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerInventoryClick extends BaseListener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(p));
		if (user.isBuildMode()) {
			return;
		}
		e.setCancelled(true);
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		String itemid = Item.getItemId(item);
		language:
		{
			String languageId = ItemBuilder.item(item).persistentDataStorage()
					.get(InventorySettings.language, PersistentDataTypes.STRING);
			if (languageId == null || languageId.isEmpty())
				break language;
			Language language = Language.fromString(languageId);
			int i = 0;
			for (; i < Language.values().length; i++) {
				if (Language.values()[i] == language) {
					i++;
					break;
				}
			}
			i %= Language.values().length;
			language = Language.values()[i];
			user.getUser().setLanguage(language);
			boolean oldS = user.isSounds();
			user.setSounds(false);
			Lobby.getInstance().setItems(user);
			user.setGadget(user.getGadget());
			user.setOpenInventory(new InventorySettings(user.getUser()));
			p.setFlying(true);
			user.setSounds(oldS);
		}
		if (itemid == null || itemid.isEmpty()) {
			return;
		}
		boolean close = false;
		if (itemid.equals(Item.INVENTORY_COMPASS_SPAWN.getItemId())) {
			user.teleport(Lobby.getInstance().getDataManager().getSpawn());
			close = true;
		} else if (itemid.equals(Item.INVENTORY_COMPASS_WOOLBATTLE.getItemId())
				&& !(user.getOpenInventory() instanceof InventoryWoolBattle)) {
			user.teleport(Lobby.getInstance().getDataManager().getWoolBattleSpawn());
			close = true;
		} else if (itemid.equals(Item.INVENTORY_COMPASS_JUMPANDRUN.getItemId())) {
			user.teleport(Lobby.getInstance().getDataManager().getJumpAndRunSpawn());
			close = true;
		} else if (itemid.equals(Item.INVENTORY_SETTINGS_ANIMATIONS_ON.getItemId())) {
			user.setAnimations(false);
			user.setOpenInventory(new InventorySettings(user.getUser()));
		} else if (itemid.equals(Item.INVENTORY_SETTINGS_ANIMATIONS_OFF.getItemId())) {
			user.setAnimations(true);
			user.setOpenInventory(new InventorySettings(user.getUser()));
		} else if (itemid.equals(Item.INVENTORY_SETTINGS_SOUNDS_ON.getItemId())) {
			user.setSounds(false);
			user.setOpenInventory(new InventorySettings(user.getUser()));
		} else if (itemid.equals(Item.INVENTORY_SETTINGS_SOUNDS_OFF.getItemId())) {
			user.setSounds(true);
			user.setOpenInventory(new InventorySettings(user.getUser()));
		}

		// PagedInventories
		IInventory inv = user.getOpenInventory();

		if (inv instanceof InventoryConfirm) {
			InventoryConfirm cinv = (InventoryConfirm) inv;
			if (itemid.equals(Item.CONFIRM.getItemId())) {
				cinv.onConfirm.run();
			} else if (itemid.equals(Item.CANCEL.getItemId())) {
				cinv.onCancel.run();
			}
		}

		if (inv instanceof InventoryPServer) {
			if (itemid.equals(Item.INVENTORY_PSERVER_PRIVATE.getItemId())) {
				user.setOpenInventory(new InventoryPServerOwn(user.getUser()));
			}
		} else if (inv instanceof InventoryPServerOwn) {
			if (itemid.equals(Item.INVENTORY_PSERVER_PUBLIC.getItemId())) {
				user.setOpenInventory(new InventoryPServer(user));
			}
		}
		if (close) {
			p.closeInventory();
		}
	}
}
