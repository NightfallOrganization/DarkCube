///*
// * Copyright (c) 2024. [DarkCube]
// * All rights reserved.
// * You may not use or redistribute this software or any associated files without permission.
// * The above copyright notice shall be included in all copies of this software.
// */
//
//package eu.darkcube.system.aetheria.other;
//
//import eu.darkcube.system.aetheria.manager.WorldManager;
//import eu.darkcube.system.aetheria.manager.monster.EntityTypeManager;
//import eu.darkcube.system.aetheria.manager.monster.MonsterCreationManager;
//import eu.darkcube.system.aetheria.manager.monster.MonsterSpawnManager;
//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.entity.EntityType;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.Chunk;
//import java.util.Random;
//
//public class MonsterSpawnTask extends BukkitRunnable {
//
//    private MonsterSpawnManager monsterSpawnManager;
//    private MonsterCreationManager monsterCreationManager;
//    private World monsterWorld;
//    private Random random = new Random();
//
//    public MonsterSpawnTask(MonsterSpawnManager monsterSpawnManager, MonsterCreationManager monsterCreationManager) {
//        this.monsterSpawnManager = monsterSpawnManager;
//        this.monsterCreationManager = monsterCreationManager;
//        this.monsterWorld = WorldManager.MONSTERWORLD;
//    }
//
//    @Override
//    public void run() {
//        // Für jeden Monster-Typ im MonsterCreationManager
//        for (MonsterCreationManager.Monster monster : monsterCreationManager.getMonsters()) {
//            EntityTypeManager.EntityType entityType = monster.getEntityType();
//
//            // Prüfen, ob Monster des Typs bereits existieren
//            boolean monsterExists = monsterWorld.getEntities().stream()
//                    .anyMatch(entity -> entity.getType() == EntityType.valueOf(entityType.name()));
//
//            // Wenn kein Monster des Typs existiert, spawnen
//            if (!monsterExists) {
//                Location location = getRandomLocationInLoadedChunks();
//                if(location != null) {
//                    monsterSpawnManager.spawnMonster(monsterWorld, location, entityType, 1); // Menge auf 1 gesetzt
//                }
//            }
//        }
//    }
//
//    private Location getRandomLocationInLoadedChunks() {
//        Chunk[] loadedChunks = monsterWorld.getLoadedChunks();
//        if (loadedChunks.length == 0) return null;
//
//        Chunk chunk = loadedChunks[random.nextInt(loadedChunks.length)];
//        int x = chunk.getX() * 16 + random.nextInt(16);
//        int z = chunk.getZ() * 16 + random.nextInt(16);
//        int y = monsterWorld.getHighestBlockYAt(x, z);
//
//        return new Location(monsterWorld, x, y, z);
//    }
//}
