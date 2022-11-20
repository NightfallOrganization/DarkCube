package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.user.UserSettings;
import eu.darkcube.minigame.woolbattle.util.InventoryId;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;

public class ListenerInventoryClick extends Listener<InventoryClickEvent> {

	@Override
	@EventHandler
	public void handle(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		if (itemid == null) {
			return;
		}
		if (user.getOpenInventory() == InventoryId.SETTINGS) {
			e.setCancelled(true);
			if (itemid.equals(Item.SETTINGS_HEIGHT_DISPLAY.getItemId())) {
				UserSettings.openHeightDisplay(user);
			} else if (itemid.equals(Item.SETTINGS_WOOL_DIRECTION.getItemId())) {
				UserSettings.openWoolDirection(user);
			}
		} else if (user.getOpenInventory() == InventoryId.HEIGHT_DISPLAY) {
			e.setCancelled(true);
			if (itemid.equals(Item.SETTINGS_HEIGHT_DISPLAY_COLOR.getItemId())) {
				UserSettings.openHeightDisplayColor(user);
			} else if (itemid.equals(Item.HEIGHT_DISPLAY_ON.getItemId())) {
				user.getData().getHeightDisplay().setEnabled(false);
				UserSettings.setInventoryHeightDisplayToggled(user, e.getClickedInventory());
			} else if (itemid.equals(Item.HEIGHT_DISPLAY_OFF.getItemId())) {
				user.getData().getHeightDisplay().setEnabled(true);
				UserSettings.setInventoryHeightDisplayToggled(user, e.getClickedInventory());
			}
		} else if (user.getOpenInventory() == InventoryId.HEIGHT_DISPLAY_COLOR) {
			if (itemid.equals(UserSettings.COLOR_SELECTION_ID)) {
				char colorChar = ItemManager.getId(item, "color").charAt(0);
				ChatColor c = ChatColor.getByChar(colorChar);
				user.getData().getHeightDisplay().setColor(c);
				WoolBattle.getInstance().getIngame().schedulerHeightDisplay.display(p);
			}
			e.setCancelled(true);
		} else if (user.getOpenInventory() == InventoryId.WOOL_DIRECTION) {
			if (itemid.equals(Item.SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT.getItemId())) {
				user.getData().setWoolSubtractDirection(WoolSubtractDirection.LEFT_TO_RIGHT);
			} else if (itemid.equals(Item.SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT.getItemId())) {
				user.getData().setWoolSubtractDirection(WoolSubtractDirection.RIGHT_TO_LEFT);
			}
			e.setCancelled(true);
			UserSettings.openWoolDirection(user);
		}
		if (WoolBattle.getInstance().getLobby().isEnabled() && e.getHotbarButton() != -1) {
			e.setCancelled(true);
		}

		if (e.isCancelled()) {
			return;
		}
		if (e.getRawSlot() != -1 && e.getRawSlot() != -999) {
			EventInteract pe = new EventInteract(p, e.getCurrentItem(),
					e.getClickedInventory(), e.getClick());
			Bukkit.getPluginManager().callEvent(pe);
			e.setCancelled(pe.isCancelled());
			e.setCurrentItem(pe.getItem());
		} else {
			e.setCancelled(true);
		}
	}
}