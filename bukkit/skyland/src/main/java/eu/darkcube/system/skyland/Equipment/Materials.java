/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public enum Materials {

	DRAGON_SCALE(new PlayerStats[] {new PlayerStats(PlayerStatsType.ARMOR, 100)}, Rarity.RARE,
			new ItemStack(Material.DIAMOND_AXE), 10, 40),
	TESTING_IRON(new PlayerStats[] {new PlayerStats(PlayerStatsType.DAMAGE, 10)}, Rarity.RARE,
			new ItemStack(Material.DIAMOND_AXE), 10, 40),//beispiel

	;

	private PlayerStats[] stats;
	private Rarity rarity;
	private ItemStack model;
	private int lvlReq;

	//private static HashMap<Rarity, ArrayList<Materials>> materials = new HashMap<>();
	Materials(PlayerStats[] stats, Rarity rarity, ItemStack model, int durability, int lvlReq) {
		this.stats = stats;
		this.rarity = rarity;
		this.model = model;
		this.model.setDurability((short) durability);
		this.lvlReq = lvlReq;

/*        if(!getMaterials().containsKey(rarity)){
            getMaterials().put(rarity, new ArrayList<>());
        }

        Materials.getMaterials().get(rarity).add(this);*/

	}

/*    public static HashMap<Rarity, ArrayList<Materials>> getMaterials() {
        return materials;
    }*/

	public static Materials getRandomMaterial(HashMap<Rarity, ArrayList<Materials>> materials) {
		Rarity r = Rarity.rollRarity(materials);
		ArrayList<Materials> materials1 = materials.get(r);
		Random random = new Random();
		int i = random.nextInt(materials1.size());
		return materials1.get(i);
	}

	public PlayerStats[] getStats() {
		return stats;
	}

	public int getLvlReq() {
		return lvlReq;
	}
}
