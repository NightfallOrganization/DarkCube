package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryConfirm;
import eu.darkcube.system.lobbysystem.inventory.InventorySettings;
import eu.darkcube.system.lobbysystem.inventory.InventoryWoolBattle;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.PagedInventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.PagedInventoryOld;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServer;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerOwn;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;

public class ListenerInventoryClick extends BaseListener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		User user = UserWrapper.getUser(p.getUniqueId());
		if (user.isBuildMode()) {
			return;
		}
		e.setCancelled(true);
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		String itemid = Item.getItemId(item);
		language: {
			String languageId = new ItemBuilder(item).getUnsafe().getString("language");
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
			user.setLanguage(language);
			boolean oldS = user.isSounds();
			user.setSounds(false);
			ListenerSettingsJoin.instance.handle(new PlayerJoinEvent(p, "NoSpawnTeleport"));
			user.setGadget(user.getGadget());
			user.setOpenInventory(new InventorySettings(user));
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
		} else if (itemid.equals(Item.INVENTORY_SETTINGS_ANIMATIONS_ON.getItemId())) {
			user.setAnimations(false);
			user.setOpenInventory(new InventorySettings(user));
		} else if (itemid.equals(Item.INVENTORY_SETTINGS_ANIMATIONS_OFF.getItemId())) {
			user.setAnimations(true);
			user.setOpenInventory(new InventorySettings(user));
		} else if (itemid.equals(Item.INVENTORY_SETTINGS_SOUNDS_ON.getItemId())) {
			user.setSounds(false);
			user.setOpenInventory(new InventorySettings(user));
		} else if (itemid.equals(Item.INVENTORY_SETTINGS_SOUNDS_OFF.getItemId())) {
			user.setSounds(true);
			user.setOpenInventory(new InventorySettings(user));
		}

		// PagedInventories
		Inventory inv = user.getOpenInventory();
		if (inv instanceof PagedInventoryOld) {
			PagedInventoryOld pinv = (PagedInventoryOld) inv;
			if (itemid.equals(Item.NEXT.getItemId())) {
				pinv.setPage(user, pinv.getPage(user) + 1);
			} else if (itemid.equals(Item.PREV.getItemId())) {
				pinv.setPage(user, pinv.getPage(user) - 1);
			}
		} else if (inv instanceof PagedInventory) {
			PagedInventory pinv = (PagedInventory) inv;
			if (itemid.equals(Item.NEXT.getItemId())) {
				pinv.setPage(user, pinv.getPage(user) + 1);
			} else if (itemid.equals(Item.PREV.getItemId())) {
				pinv.setPage(user, pinv.getPage(user) - 1);
			}
		}
		
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
				user.setOpenInventory(new InventoryPServerOwn(user));
			}
		} else if (inv instanceof InventoryPServerOwn) {
			if (itemid.equals(Item.INVENTORY_PSERVER_PUBLIC.getItemId())) {
				user.setOpenInventory(new InventoryPServer(user));
			}
		}

//		boolean pserverOperated = false;
//		if (itemid.equals(Item.NEXT.getItemId())) {
//			InventoryPServer.PAGE.put(user, InventoryPServer.PAGE.getOrDefault(user, 0) + 1);
//			pserverOperated = true;
//		} else if (itemid.equals(Item.PREV.getItemId())) {
//			InventoryPServer.PAGE.put(user, InventoryPServer.PAGE.getOrDefault(user, 0) - 1);
//			pserverOperated = true;
//		}
//		if (pserverOperated) {
//			Runnable runnable = InventoryPServer.RUNNABLES.get(user);
//			if (runnable != null) {
//				runnable.update();
//			}
//		}

		if (close) {
			p.closeInventory();
		}
	}
}
