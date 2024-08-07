/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum Material {

	DRAGON_SCALE(new PlayerStats[] {new PlayerStats(PlayerStatsType.ARMOR, 100)}, Rarity.RARE,
			new ItemStack(org.bukkit.Material.DIAMOND_AXE), 10, 40),
	TESTING_IRON(new PlayerStats[] {new PlayerStats(PlayerStatsType.DAMAGE, 10)}, Rarity.RARE,
			new ItemStack(org.bukkit.Material.DIAMOND_AXE), 10, 40),//beispiel

	;

	NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), "materialID");

	private PlayerStats[] stats;
	private Rarity rarity;
	private ItemStack model;
	private int lvlReq;

	//private static HashMap<Rarity, ArrayList<Materials>> materials = new HashMap<>();
	Material(PlayerStats[] stats, Rarity rarity, ItemStack model, int durability, int lvlReq) {
		this.stats = stats;
		this.rarity = rarity;
		this.model = model;
		this.model.setDurability((short) durability);
		this.lvlReq = lvlReq;

		ItemMeta im = model.getItemMeta();
		im.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, toString());
		im.setDisplayName(toString());
		model.setItemMeta(im);
		setModelLore();


/*        if(!getMaterials().containsKey(rarity)){
            getMaterials().put(rarity, new ArrayList<>());
        }

        Materials.getMaterials().get(rarity).add(this);*/

	}


	protected ArrayList<String> setModelLore() {
		ArrayList<String> out = new ArrayList<>();
		out.add("");
		out.add("§7§m      §7« §bStats when crafted onto a piece of Equipment §7»§m      ");
		out.add("");

		for (PlayerStats pl : getStats()) {
			if (pl.getMenge() > 0) {
				out.add(pl.getType() + " §a+" + pl.getMenge());
			} else {
				out.add(pl.getType() + " §c-" + pl.getMenge());
			}

		}
		out.add("");
		out.add("§7§m      §7« §dReqir §7»§7§m      ");
		out.add("");
		out.add("Level §a" + lvlReq);
		out.add("Rarity " + rarity.getPrefix() + rarity);

        /*out.add("");
        out.add("§7§m      §7« §eSmith §7»§7§m      ");
        out.add("");*/

		model.setLore(out);
		return out;
	}


/*    public static HashMap<Rarity, ArrayList<Materials>> getMaterials() {
        return materials;
    }*/

	public static Material getRandomMaterial(List<Material> materials){
		int totalWeight = 0;
		for (Material m : materials) {
			totalWeight += m.getRarity().getWeight();
		}

		System.out.println("total weight: " + totalWeight);

		Random random = new Random();
		int roll = random.nextInt(totalWeight);
		System.out.println("roll: " + roll);
		for (Material m: materials) {
			if (roll <= m.getRarity().getWeight()) {
				return m;
			}
			System.out.println("roll now: " + roll);
			roll = roll - m.getRarity().getWeight();
		}

		//this should never occur
		return null;
	}


	public PlayerStats[] getStats() {
		return stats;
	}

	public int getLvlReq() {
		return lvlReq;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public ItemStack getModel() {
		return model;
	}
}
