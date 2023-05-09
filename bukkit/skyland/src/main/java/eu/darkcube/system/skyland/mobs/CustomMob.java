/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.mobs;

		import eu.darkcube.system.skyland.Equipment.Materials;
		import eu.darkcube.system.skyland.Equipment.Rarity;
		import eu.darkcube.system.skyland.Skyland;
		import eu.darkcube.system.skyland.SkylandClassSystem.SkylandEntity;
		import org.bukkit.Location;
		import org.bukkit.NamespacedKey;
		import org.bukkit.entity.Entity;
		import org.bukkit.entity.Mob;

		import java.util.ArrayList;
		import java.util.HashMap;
		import java.util.List;

public interface CustomMob extends SkylandEntity {

	Mob getMob();
	void aiTick();

	List<Materials> getLootTable();//todo maybe different format!

	static NamespacedKey getCustomMobTypeKey() {
		return new NamespacedKey(Skyland.getInstance(), "mobTypeId");
	}

}
