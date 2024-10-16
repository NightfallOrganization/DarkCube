/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.skyland.Skyland;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Weapons extends Equipments implements Weapon {

	static NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), "EquipInfo");
	//todo add ability
	//Ability ability;

	private Weapons(int haltbarkeit, ItemStack model, Rarity rarity,
			ArrayList<Components> components, EquipmentType equipmentType, Ability ability) {
		super(haltbarkeit, model, rarity, components, equipmentType);
		//this.ability = ability;


	}


	public static Weapons createEquipent(int haltbarkeit, ItemStack model, Rarity rarity, int lvl,
			ArrayList<Components> components, EquipmentType equipmentType, Ability ability){
		Weapons eq = new Weapons(haltbarkeit, model, rarity, components, equipmentType, ability);

		eq.saveMetaData();
		return eq;
	}

	private int zuweisungPlusHaltbarkeit(int haltbarkeit, Ability ability){
		//this.ability = ability;
		return haltbarkeit;
	}

/*    public Weapons(ItemStack itemStack) {
        super();

    }*/

	public static Weapons loadFromItem(ItemStack itemStack) {

		//itemStack.getItemMeta().getPersistentDataContainer().set(namespacedKey,
		// PersistentDataType.STRING, "test");
		if (itemStack.getItemMeta() == null ){
			System.out.println("item meta is null?! why though");
			return null;
		}

		if (itemStack.getItemMeta().getPersistentDataContainer()
				.has(namespacedKey, PersistentDataType.STRING)) {
			System.out.println("Key found");
			String s = itemStack.getItemMeta().getPersistentDataContainer()
					.get(namespacedKey, PersistentDataType.STRING);
			Gson gson = new Gson();
			Weapons weapons = new Gson().fromJson(s, Weapons.class);
			weapons.setModel(itemStack);
			weapons.setModelLore();
			return weapons;
		}
		System.out.println("key vaL: " + itemStack.getItemMeta().getPersistentDataContainer()
				.get(namespacedKey, PersistentDataType.STRING));
		System.out.println("nenene null warum");
		return null;
	}

	@Override
	public int getDamage() {
		int out = 0;

		for (Components c : super.components) {

			for (PlayerStats ps : c.getPStats()) {
				if (ps.getType() == PlayerStatsType.DAMAGE) {
					System.out.println("dmg: " + ps.getMenge());
					out += ps.getMenge();
				}
			}

		}
		return out;
	}

	//@Override
	//public Ability getAbility() {
	//	return ability;
	//}

	//this method converts this class into a string to save on an item
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
