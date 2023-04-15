/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkylandPlayer implements SkylandEntity {

	ArrayList<SkylandPlayerClass> skylandPlayerClasses;
	SkylandPlayerClass activeClass;
	Player player;
	int money;
	//todo

	public SkylandPlayer() {
		//todo
	}

	public static SkylandPlayer parseFromString(String s) {
		//todo
		return null;
	}

	private ArrayList<SkylandPlayerClass> loadSkylandPlayerClasses() {
		//todo
		player.getMetadata("skylandPlayer").get(0).asString();
		return null;
	}

	public Player getPlayer() {
		return player;
	}

	public List<Equipment> getEquipment() {
		ArrayList<Equipment> out = new ArrayList<>();
		for (ItemStack i : player.getEquipment().getArmorContents()) {
			Equipments equipments = Equipments.loadFromItem(i);
			if (equipments != null) {
				if (activeClass.getsClass().allowedEquip.contains(equipments.getEquipmentType())) {
					out.add(equipments);
				}

			}
		}

		Equipments equipments = Equipments.loadFromItem(player.getInventory().getItemInMainHand());
		if (equipments != null) {
			if (activeClass.getsClass().allowedEquip.contains(equipments.getEquipmentType())) {
				out.add(equipments);
			}
		}
		//todo get from inv need duckness for additional slot
		return out;
	}

	public Weapon getActiveWeapon() {
		//returns null if there isnt a weapon in the main hand
		Weapons equipments = Weapons.loadFromItem(player.getInventory().getItemInMainHand());
		if (equipments != null) {
			if (activeClass.getsClass().allowedEquip.contains(equipments.getEquipmentType())) {
				return equipments;
			}
		}
		return null;
	}

	@Override
	public PlayerStats[] getStats() {

		getEquipment(); //todo calc

		return new PlayerStats[0];
	}

	@Override
	public int getAttackDmg() {
		int strength = 0;
		int strengthPercDmgIncrease = 5;

		for (PlayerStats ps : getStats()) {
			if (ps.getType() == PlayerStatsType.STRENGHT) {
				strength += ps.getMenge();
			}
		}

		//here strength effectiveness is calculated
		return getActiveWeapon().getDamage() * (1 + (strength * strengthPercDmgIncrease / 100));
	}

}
