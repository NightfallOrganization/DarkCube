/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.EquipmentType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;

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
