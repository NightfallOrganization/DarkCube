/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment;

import org.bukkit.inventory.ItemStack;

public interface Equipment {

	int getHaltbarkeit();

	void setHaltbarkeit(int haltbarkeit);

	ItemStack getModel();

	void setModel(ItemStack model);

	Rarity getRarity();

	void setRarity();

	int getLvl();

	void setLvl(int lvl);

	PlayerStats[] getStats();

	void setStats(PlayerStats[] stats);

	void addComponent(Components components);

}
