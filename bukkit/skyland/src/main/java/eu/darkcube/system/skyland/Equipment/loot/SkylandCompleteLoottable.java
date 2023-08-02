/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment.loot;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkylandCompleteLoottable implements SkylandLoottable{


	List<SkylandLoottable> skylandLoottables;

	@Override
	public ItemStack[] getLoot() {
		List<ItemStack> out = new ArrayList<>();

		for (SkylandLoottable skylandLoottable : skylandLoottables){
			for (ItemStack it : skylandLoottable.getLoot()){
				if (it != null){
					out.add(it);
				}
			}

		}

		return out.toArray(new ItemStack[0]);
	}
}
