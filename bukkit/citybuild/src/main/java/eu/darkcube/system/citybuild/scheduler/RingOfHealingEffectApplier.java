/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.scheduler;

import eu.darkcube.system.citybuild.listener.Citybuild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RingOfHealingEffectApplier extends BukkitRunnable {
	private static final int RING_OF_HEALING_MODEL_DATA = 5;

	private final Citybuild plugin;

	public RingOfHealingEffectApplier(Citybuild plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (isRingOfHealing(player.getInventory().getItemInMainHand())) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 0, false, false, false));
			} else if (player.hasPotionEffect(PotionEffectType.REGENERATION)) {
				player.removePotionEffect(PotionEffectType.REGENERATION);
			}
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
