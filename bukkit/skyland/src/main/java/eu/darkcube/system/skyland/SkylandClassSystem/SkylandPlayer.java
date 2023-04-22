/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.*;
import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.staticval.Globals;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SkylandPlayer implements SkylandEntity {

	private static final Key MONEY_KEY = new Key(Skyland.getInstance(), "money");
	private final User user;
	private final ArrayList<SkylandPlayerClass> skylandPlayerClasses = new ArrayList<>();
	private SkylandPlayerClass activeClass;
	//todo

	public SkylandPlayer(User user) {
		this.user = user;
		user.getPersistentDataStorage().setIfNotPresent(MONEY_KEY, PersistentDataTypes.BIGINTEGER, BigInteger.ZERO);
	}



	public List<Equipment> getEquipment() {
		ArrayList<Equipment> out = new ArrayList<>();
		for (ItemStack i : getPlayer().getEquipment().getArmorContents()) {
			Equipments equipments = Equipments.loadFromItem(i);
			if (equipments != null) {
				if (activeClass.getsClass().allowedEquip.contains(equipments.getEquipmentType())) {
					out.add(equipments);
				}

			}
		}

		Equipments equipments = Equipments.loadFromItem(getPlayer().getInventory().getItemInMainHand());
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
		Weapons equipments = Weapons.loadFromItem(getPlayer().getInventory().getItemInMainHand());
		if (equipments != null) {
			if (activeClass.getsClass().allowedEquip.contains(equipments.getEquipmentType())) {
				return equipments;
			}
		}
		return null;
	}

	@Override
	public PlayerStats[] getStats() {
		ArrayList<PlayerStats> playerStats = new ArrayList<>();
		//playerStats.add(activeClass);

		getEquipment(); //todo calc

		return new PlayerStats[0];
	}

	@Override
	public int getAttackDmg() {
		int strength = 0;

		for (PlayerStats ps : getStats()) {
			if (ps.getType() == PlayerStatsType.STRENGHT) {
				strength += ps.getMenge();
			}
		}

		//here strength effectiveness is calculated
		return getActiveWeapon().getDamage() * (1 + (strength * Globals.strengthDmgMult / 100));
	}

	public User getUser() {
		return user;
	}

	public Player getPlayer() {
		return user.asPlayer();
	}

	public ArrayList<SkylandPlayerClass> getSkylandPlayerClasses() {
		return skylandPlayerClasses;
	}

	public SkylandPlayerClass getActiveClass() {
		return activeClass;
	}

	public void setActiveClass(SkylandPlayerClass activeClass) {
		this.activeClass = activeClass;
	}

	public BigInteger getMoney() {
		return user.getPersistentDataStorage().get(MONEY_KEY, PersistentDataTypes.BIGINTEGER);
	}

	public void setMoney(BigInteger money) {
		user.getPersistentDataStorage().set(MONEY_KEY, PersistentDataTypes.BIGINTEGER, money);
	}
}
