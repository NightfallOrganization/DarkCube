/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.skyland.Skyland;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Equipment implements EquipmentInterface {

	static NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), "EquipInfo");
	int haltbarkeit = 0;
	transient ItemStack model;
	Rarity rarity;
	ArrayList<Component> components;
	EquipmentType equipmentType;

	protected Equipment(int haltbarkeit, ItemStack model, Rarity rarity,
						ArrayList<Component> components, EquipmentType equipmentType) {
		//todo make sure saveMetaData is called on Creation
		this.haltbarkeit = haltbarkeit;
		this.model = model;
		this.rarity = rarity;
		this.components = components;
		this.equipmentType = equipmentType;
		setModelLore();
	}

	public void saveMetaData() {
		ItemMeta meta = model.getItemMeta();
		meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING,
				toString());

		//meta.setDisplayName(rarity.getPrefix() + model.getItemMeta().getDisplayName());
		model.setItemMeta(meta);
		System.out.println("meta set");
		if (model.getItemMeta() == null){
			System.out.println("meta is null");
		}
	}

	public static Equipment createEquipment(int haltbarkeit, ItemStack model, Rarity rarity,
											ArrayList<Component> components, EquipmentType equipmentType) {
		Equipment eq = new Equipment(haltbarkeit, model, rarity, components, equipmentType);

		eq.saveMetaData();
		return eq;
	}

	public static Equipment loadFromItem(ItemStack itemStack) {
		if (itemStack != null) {
			if (itemStack.getItemMeta().getPersistentDataContainer()
					.has(namespacedKey, PersistentDataType.STRING)) {
				System.out.println("Key found");
				String s = itemStack.getItemMeta().getPersistentDataContainer()
						.get(namespacedKey, PersistentDataType.STRING);
				Gson gson = new Gson();

				Equipment eq = gson.fromJson(s, Equipment.class);
				eq.setModel(itemStack);
				eq.setModelLore();
				return eq;

			} else {
				System.out.println("itemStack is null");
				return null;
			}

			//itemStack.getItemMeta().getPersistentDataContainer().set(namespacedKey,
			// PersistentDataType.STRING, "test");

		}
		return null;
	}

	protected ArrayList<String> setModelLore() {
		ArrayList<String> out = new ArrayList<>();
		out.add("");
		out.add("§7§m      §7« §bStats §7»§m      ");
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
		out.add("Level §a" + getLvl());
		out.add("Rarity " + rarity.getPrefix() + rarity);

        /*out.add("");
        out.add("§7§m      §7« §eSmith §7»§7§m      ");
        out.add("");*/

		model.setLore(out);
		return out;
	}

	//this method converts this class into a string to save on an item
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	//todo to string and parse from string

	@Override
	public int getHaltbarkeit() {
		return haltbarkeit;
	}

	@Override
	public void setHaltbarkeit(int haltbarkeit) {
		this.haltbarkeit = haltbarkeit;
	}

	@Override
	public ItemStack getModel() {
		return model;
	}

	@Override
	public void setModel(ItemStack model) {
		this.model = model;
	}

	@Override
	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public void setRarity() {
		this.rarity = rarity;
	}

	@Override
	public int getLvl() {
		int highestLvl = 0;
		for (Component c:components) {
			if (highestLvl< c.getMaterialType().getLvlReq()){
				highestLvl = c.getMaterialType().getLvlReq();
			}
		}
		return highestLvl;
	}



	@Override
	public PlayerStats[] getStats() {

		HashMap<PlayerStatsType, Integer> temp = new HashMap<>();

		for (Component c : components) {

			for (PlayerStats p : c.getPStats()) {
				if (!temp.containsKey(p.getType())) {
					temp.put(p.getType(), p.getMenge());
				} else {
					temp.put(p.getType(), temp.get(p.getType()) + p.getMenge());
				}
			}

		}

		PlayerStats[] out = new PlayerStats[temp.keySet().size()];
		AtomicInteger i = new AtomicInteger();
		temp.forEach((playerStatsType, integer) -> {
			out[i.get()] = new PlayerStats(playerStatsType, integer);
			i.getAndIncrement();
		});

		//kann optimiert werden durch cachen
		return out;
	}

	@Override
	public void setStats(PlayerStats[] stats) {

	}

	@Override
	public void addComponent(Component component) {
		this.components.add(component);
	}

	public EquipmentType getEquipmentType() {
		return equipmentType;
	}
}
