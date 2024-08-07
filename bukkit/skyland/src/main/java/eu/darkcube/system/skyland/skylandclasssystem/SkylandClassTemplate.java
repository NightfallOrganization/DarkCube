/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.skylandclasssystem;

import eu.darkcube.system.skyland.equipment.EquipmentType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum SkylandClassTemplate {

	ARCHER(new EquipmentType[] {EquipmentType.AXE}), WARRIOR(new EquipmentType[] {EquipmentType.AXE});

	EquipmentType[] allowedEquip;

	SkylandClassTemplate(EquipmentType[] allowedEquip) {
		this.allowedEquip = allowedEquip;
	}

	public EquipmentType[] getAllowedEquip() {
		return allowedEquip;
	}
	public ItemStack getDisplay(){
		ItemStack out = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta re = out.getItemMeta();
		re.setDisplayName(this.name());
		out.setItemMeta(re);

		//todo
		return out;
	}

}
