package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnderBagListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (item != null && item.getType() == Material.FIREWORK_STAR) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 4) {
				// Copy the items from the player's EnderChest to the custom inventory
				Inventory enderChest = Bukkit.createInventory(null, 27, "§f\uDAFF\uDFEFḉ");
				enderChest.setContents(event.getPlayer().getEnderChest().getContents());

				event.getPlayer().openInventory(enderChest);
				event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.5f, 1.0f);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getView().getTitle().equals("§f\uDAFF\uDFEFḉ")) {
			// Copy the items from the custom inventory back to the player's EnderChest
			event.getPlayer().getEnderChest().setContents(event.getInventory().getContents());

			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 0.5f, 1.0f);
		}
	}
}
