/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.*;
import eu.darkcube.system.skyland.staticval.Globals;
import eu.darkcube.system.userapi.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkylandPlayer implements SkylandEntity {

	private final User user;
	private final ArrayList<SkylandPlayerClass> skylandPlayerClasses = new ArrayList<>();
	private SkylandPlayerClass activeClass;
	private int money = 0;
	//todo

	public SkylandPlayer(User user) {
		this.user = user;
	}

	public static SkylandPlayer parseFromString(String s, Player p) {
		SkylandPlayer out = new SkylandPlayer(p);

		String[] in = s.split("-");
		out.setMoney(Integer.parseInt(in[1]));
		System.out.println("money " + out.getMoney());//todo
		for (int i = 4; i < in.length; i++) {
			out.getSkylandPlayerClasses().add(SkylandPlayerClass.parseString(in[i], out));
		}

		if (Integer.parseInt(in[3]) >= 0) {
			out.setActiveClass(out.getSkylandPlayerClasses().get(Integer.parseInt(in[3])));
		}

		return out;
	}

	@Override
	public String toString() {

		String out = "money: -" + money + "- " + "active class: ";
		for (int i = 0; i < skylandPlayerClasses.size(); i++) {
			if (skylandPlayerClasses.get(i).equals(activeClass)) {
				out += "-" + i + "-";
				break;
			}
		}

		if (skylandPlayerClasses.size() == 0) {
			out += "-" + -1 + "-";
		}

		for (SkylandPlayerClass skylandPlayerClass : skylandPlayerClasses) {
			out += skylandPlayerClass.toString() + "-";
		}
		System.out.println("toString out: " + out);
		return out;
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

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
}
