/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment;

import org.bukkit.inventory.ItemStack;

public interface EquipmentInterface {

	int getHaltbarkeit();

	void setHaltbarkeit(int haltbarkeit);

	ItemStack getModel();

	void setModel(ItemStack model);

	Rarity getRarity();

	void setRarity();

	int getLvl();

	PlayerStats[] getStats();

	void setStats(PlayerStats[] stats);

	void addComponent(Component component);

	EquipmentType getEquipmentType();

}
