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
		import org.bukkit.entity.EntityType;
		import org.bukkit.entity.Mob;
		import org.bukkit.persistence.PersistentDataType;
		import org.bukkit.util.Vector;

		import java.util.ArrayList;
		import java.util.HashMap;
		import java.util.List;

public interface CustomMob extends SkylandEntity {

	void updateName();

	Mob getMob();
	void aiTick();
	EntityType getType();


	default void spawnMob(Location location, EntityType entityType){
		Mob mob = (Mob) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		mob.getPersistentDataContainer().set(getCustomMobTypeKey(), PersistentDataType.INTEGER, -1);
	}

	List<Materials> getLootTable();//todo maybe different format!

	static NamespacedKey getCustomMobTypeKey() {
		return new NamespacedKey(Skyland.getInstance(), "mobTypeId");
	}

}
