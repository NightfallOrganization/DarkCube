package eu.darkcube.system.citybuild.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RingOfHealingSwapHandler implements Listener {
	private static final int RING_OF_HEALING_MODEL_DATA = 5;

	@EventHandler
	public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getOffHandItem();

		if (isRingOfHealing(item)) {
			event.setCancelled(true);
		}
	}

	private boolean isRingOfHealing(ItemStack item) {
		if (item != null && item.getType() == Material.FIREWORK_STAR) {
			ItemMeta meta = item.getItemMeta();
			return meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == RING_OF_HEALING_MODEL_DATA;
		}
		return false;
	}
}
