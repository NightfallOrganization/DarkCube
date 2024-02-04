/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.other;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.manager.RarityManager;
import eu.darkcube.system.aetheria.manager.WorldManager;
import eu.darkcube.system.aetheria.manager.monster.EntityTypeManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterCreationManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterSpawnManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Chunk;

import java.util.Collection;
import java.util.Random;

public class MonsterSpawnTask {

    private MonsterSpawnManager monsterSpawnManager;
    private MonsterCreationManager monsterCreationManager;
    private World monsterWorld;
    private Random random = new Random();

    public MonsterSpawnTask(MonsterSpawnManager monsterSpawnManager, MonsterCreationManager monsterCreationManager) {
        this.monsterSpawnManager = monsterSpawnManager;
        this.monsterCreationManager = monsterCreationManager;
        this.monsterWorld = WorldManager.MONSTERWORLD;
    }

    public void startSpawning() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Für jeden Monster-Typ im MonsterCreationManager
                for (MonsterCreationManager.MonsterType monster : monsterCreationManager.getMonsters()) {
                    EntityTypeManager.EntityType entityType = monster.getEntityType();

                    Location location = getRandomLocationInLoadedChunks();
                    if(location != null) {
                        // Prüfe, ob Entities im Umkreis von 20 Blöcken vorhanden sind
                        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 20, 20, 20);
                        if (nearbyEntities.isEmpty()) {

                            RarityManager.Rarity rarity = monster.getRarity();
                            monsterSpawnManager.spawnMonster(monsterWorld, location, entityType, rarity);
                        }
                    }
                }
            }
        }.runTaskTimer(Aetheria.getInstance(), 20L, 1L);
    }


    private Location getRandomLocationInLoadedChunks() {
        Chunk[] loadedChunks = monsterWorld.getLoadedChunks();
        if (loadedChunks.length == 0) return null;

        Chunk chunk = loadedChunks[random.nextInt(loadedChunks.length)];
        int x = chunk.getX() * 16 + random.nextInt(16);
        int z = chunk.getZ() * 16 + random.nextInt(16);
        int y = monsterWorld.getHighestBlockYAt(x, z);

        return new Location(monsterWorld, x, y, z);
    }
}
