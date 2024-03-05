/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.mobs;

import eu.darkcube.system.skyland.equipment.PlayerStats;
import eu.darkcube.system.skyland.equipment.PlayerStatsType;
import eu.darkcube.system.skyland.equipment.Rarity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

@Deprecated(forRemoval = true)
public class CustomZombie {

	Villager zombie;

	public CustomZombie(Location loc){
		System.out.println("zombie spawned");
		zombie = (Villager) loc.getWorld().spawnEntity(loc.add(new Vector(10, 0 ,0)), EntityType.VILLAGER);
		System.out.println("zombie should appear");
		FollowingMob fm = new FollowingMob(zombie, 5, new PlayerStats[] {new PlayerStats(
				PlayerStatsType.ARMOR, 100)}, 1, true, "zombie", Rarity.RARE, new MonsterAbility[] {new AttackAbility()});
		System.out.println("following mob ai added");

		/*
		new BukkitRunnable() {
			@Override
			public void run() {

				Bukkit.getMobGoals().removeAllGoals(zombie);
				zombie.getPathfinder().moveTo(zombie.getLocation().add(100, 0, 0));
			}
		}.runTaskTimer(Skyland.getInstance(), 20,20);

		 */


		/*
		WanderingNPCGoal zg = new WanderingNPCGoal(Skyland.getInstance(), zombie);
		if (!Bukkit.getMobGoals().hasGoal(zombie, zg.getKey())) {
			Bukkit.getMobGoals().addGoal(zombie, 3, zg);
		}

		 */
	}




}
