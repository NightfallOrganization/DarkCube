/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.listener;

import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RingOfHealingListener implements Listener {
	private static final int RING_OF_HEALING_MODEL_DATA = 5;
	private Aetheria plugin;

	public RingOfHealingListener(Aetheria plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> handleRingOfHealingUsage(event.getPlayer()), 1L);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> handleRingOfHealingUsage(event.getPlayer()), 1L);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> handleRingOfHealingUsage((Player) event.getWhoClicked()), 1L);
		}
	}

	private void handleRingOfHealingUsage(Player player) {
		if (isRingOfHealing(player.getInventory().getItemInMainHand())) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, false, false));
			//player.sendTitle(ChatColor.of("#4e5c24") + "Ḋ", "", 0, 99999, 0);
		} else {
			player.removePotionEffect(PotionEffectType.REGENERATION);
			//player.sendTitle(ChatColor.of("#4e5c24") + " ", "", 0, 0, 0);
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
