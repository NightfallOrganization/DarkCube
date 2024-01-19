/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.manager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MonsterSpawnManager implements Listener {

    private World monsterWorld;
    private MonsterLevelManager monsterLevelManager;

    public MonsterSpawnManager(MonsterLevelManager monsterLevelManager) {
        this.monsterLevelManager = monsterLevelManager;
        monsterWorld = WorldManager.MONSTERWORLD;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getLocation().getWorld().equals(monsterWorld)) {
            EntityType type = event.getEntityType();
            if (type.equals(EntityType.ZOMBIE) || type.equals(EntityType.ZOMBIE_VILLAGER) ||
                    type.equals(EntityType.PILLAGER) || type.equals(EntityType.STRAY) ||
                    type.equals(EntityType.SKELETON) || type.equals(EntityType.CREEPER) ||
                    type.equals(EntityType.ENDERMAN) || type.equals(EntityType.SPIDER)) {
                int level = calculateMonsterLevel(event.getLocation());
                if (level > 0) {
                    monsterLevelManager.updateMonsterLevel((LivingEntity) event.getEntity(), level);
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private int calculateMonsterLevel(Location location) {
        // Berechnung des Abstands vom Punkt (0,0) in der Welt
        double distance = Math.sqrt(Math.pow(location.getX(), 2) + Math.pow(location.getZ(), 2));

        // Bestimmung des Levels basierend auf dem Abstand
        int level = (int) Math.ceil(distance / 150.0);

        // Sicher stellen dass das Level mindestens 1 ist
        return Math.max(level, 1);
    }

}
