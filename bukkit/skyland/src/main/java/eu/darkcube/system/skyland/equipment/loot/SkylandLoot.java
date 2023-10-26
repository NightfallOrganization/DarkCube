/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment.loot;

import org.bukkit.inventory.ItemStack;

public class SkylandLoot {

	double weight;
	ItemStack model;


	public SkylandLoot(ItemStack is, int w){
		weight = w;
		model = is;
	}

	public double getWeight() {
		return weight;
	}

	public ItemStack getModel() {
		return model;
	}
}
