/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.citybuild.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SwiftSwordListener implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			ItemStack itemInHand = player.getInventory().getItem(EquipmentSlot.HAND);
			if (isSwiftSword(itemInHand)) {
				double attackSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue();
				double baseDamage = event.getDamage();
				double newDamage = baseDamage * (attackSpeed / 100.0);  // Adjust this formula as needed
				event.setDamage(newDamage);
			}
		}
	}

	private boolean isSwiftSword(ItemStack item) {
		if (item != null && item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			return meta.hasCustomModelData() && meta.getCustomModelData() == 2;
		}
		return false;
	}
}
