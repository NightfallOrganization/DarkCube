/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment.loot;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class SkylandAmountLoot implements SkylandLoottable{

	ItemStack loot;
	int min;
	int max;

	public SkylandAmountLoot(ItemStack loot, int min, int max) {
		this.loot = loot;
		this.min = min;
		this.max = max;
	}

	@Override
	public ItemStack[] getLoot() {
		Random random = new Random();

		int roll = random.nextInt(min, max);

		loot.setAmount(roll);

		return new ItemStack[] { new ItemStack(loot)};
	}
}
