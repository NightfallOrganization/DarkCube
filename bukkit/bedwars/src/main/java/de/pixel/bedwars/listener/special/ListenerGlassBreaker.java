package de.pixel.bedwars.listener.special;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.shop.ShopItem;

public class ListenerGlassBreaker implements Listener {

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		ItemStack item = e.getItem();
		if (!ShopItem.S_GLASSBREAKER.getItemId().equals(ShopItem.getItemId(item))) {
			return;
		}
		if(e.getClickedBlock().getType() != Material.GLASS) {
			return;
		}
		e.setCancelled(true);
		BlockBreakEvent e2 = new BlockBreakEvent(e.getClickedBlock(), p);
		Bukkit.getPluginManager().callEvent(e2);
	}
}
