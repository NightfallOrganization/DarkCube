/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment.loot;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class SkylandWeightedLootGroup implements SkylandLoottable{
	List<SkylandLoot> loot;
	double chanceToDraw;
	int rndmNachkommastellen = 5;

	public SkylandWeightedLootGroup(List<SkylandLoot> loot, double chanceToDraw) {
		this.loot = loot;
		this.chanceToDraw = chanceToDraw;

	}

	public SkylandLoot roll(){
		Random random = new Random();
		int roll = random.nextInt(0, (int) Math.pow(10, rndmNachkommastellen));
		chanceToDraw = chanceToDraw *( (int) Math.pow(10, rndmNachkommastellen + 1));
		if (roll < chanceToDraw){
			int totalWeight = 0;

			for (SkylandLoot sk : loot){
				totalWeight = (int) (totalWeight + sk.getWeight());
			}

			roll = random.nextInt(0, totalWeight);

			for (SkylandLoot sk : loot){
				if (sk.getWeight() < roll){
					return sk;
				}
				roll = (int) (roll - sk.getWeight());
			}

		}else {
			return null;
		}
		return null;

	}

	@Override
	public ItemStack[] getLoot() {
		SkylandLoot skl = roll();
		if (skl != null){
			return  new ItemStack[] {skl.getModel()};
		}
		return  new ItemStack[] {};
	}
}
