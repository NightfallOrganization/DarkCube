package eu.darkcube.system.lobbysystem.listener;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.lobbysystem.inventory.InventoryCompass;
import eu.darkcube.system.lobbysystem.inventory.InventoryGadget;
import eu.darkcube.system.lobbysystem.inventory.InventoryLobbySwitcher;
import eu.darkcube.system.lobbysystem.inventory.InventorySettings;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerOwn;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;

public class ListenerInteract extends BaseListener {

	private static final Collection<Material> DENIED = Arrays.asList(new Material[] {
					Material.FENCE_GATE, Material.ACACIA_FENCE_GATE,
					Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE,
					Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE,
					Material.ACACIA_DOOR, Material.BIRCH_DOOR,
					Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR,
					Material.SPRUCE_DOOR, Material.WOOD_DOOR,
					Material.WOODEN_DOOR, Material.TRAP_DOOR,
					Material.STONE_BUTTON, Material.WOOD_BUTTON
	});

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		User user = UserWrapper.getUser(p.getUniqueId());
		if (!user.isBuildMode() && e.getAction() == Action.RIGHT_CLICK_BLOCK
						&& DENIED.contains(e.getClickedBlock().getType())) {
			e.setCancelled(true);
		}
		if (e.getAction() != Action.RIGHT_CLICK_AIR
						&& e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		ItemStack item = e.getItem();
		if (item == null) {
			return;
		}
		String itemid = Item.getItemId(item);
		if (itemid == null) {
			return;
		}
		if (itemid.equals(Item.INVENTORY_LOBBY_SWITCHER.getItemId())) {
			user.setOpenInventory(new InventoryLobbySwitcher(user));
		} else if (itemid.equals(Item.INVENTORY_COMPASS.getItemId())) {
			user.setOpenInventory(new InventoryCompass(user));
		} else if (itemid.equals(Item.INVENTORY_GADGET.getItemId())) {
			user.setOpenInventory(new InventoryGadget(user));
		} else if (itemid.equals(Item.INVENTORY_SETTINGS.getItemId())) {
			user.setOpenInventory(new InventorySettings(user));
		} else if (itemid.equals(Item.PSERVER_MAIN_ITEM.getItemId())) {
			user.setOpenInventory(new InventoryPServerOwn(user));
		}
	}
}
