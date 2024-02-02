/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.entity.EntityType;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.entity.CreatureSpawnEvent;
//
//import java.util.Random;
//
//public class MonsterSpawnManager implements Listener {
//
//    private MonsterCreationManager monsterCreationManager;
//    private Random random = new Random();
//
//    public MonsterSpawnManager(MonsterCreationManager monsterCreationManager) {
//        this.monsterCreationManager = monsterCreationManager;
//    }
//
//    // Methode zum Deaktivieren des normalen Monster Spawns
//    @EventHandler
//    public void onCreatureSpawn(CreatureSpawnEvent event) {
//        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
//            event.setCancelled(true);
//        }
//    }
//
//    // Eigene Spawnmethode
//    public void spawnMonster(World world, Location location, EntityTypeManager.EntityType entityType, int quantity) {
//        for (int i = 0; i < quantity; i++) {
//            monsterCreationManager.getMonsterByEntityType(entityType).ifPresent(monster -> {
//                EntityType bukkitEntityType = EntityType.valueOf(monster.getEntityType().name());
//                world.spawnEntity(location, bukkitEntityType);
//            });
//        }
//    }
//}






































import eu.darkcube.system.aetheria.manager.RarityManager;
import eu.darkcube.system.aetheria.manager.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Optional;

public class MonsterSpawnManager implements Listener {

    private World monsterWorld;
    private MonsterLevelManager monsterLevelManager;
    private MonsterCreationManager monsterCreationManager;

    public MonsterSpawnManager(MonsterLevelManager monsterLevelManager, MonsterCreationManager monsterCreationManager) {
        this.monsterLevelManager = monsterLevelManager;
        this.monsterCreationManager = monsterCreationManager;
        monsterWorld = WorldManager.MONSTERWORLD;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!event.getLocation().getWorld().equals(monsterWorld)) {
            return;
        }

        LivingEntity spawnedEntity = event.getEntity();

        try {
            EntityTypeManager.EntityType entityType = EntityTypeManager.EntityType.valueOf(spawnedEntity.getType().name());
            Optional<MonsterCreationManager.Monster> monsterOpt = monsterCreationManager.getMonsterByEntityType(entityType);

            if (monsterOpt.isPresent()) {
                RarityManager.Rarity rarity = monsterOpt.get().getRarity();
                Integer level = calculateMonsterLevel(event.getLocation(), rarity);
                if (level != null && monsterLevelManager.isLevelAppropriate(entityType, level)) {
                    monsterLevelManager.updateMonsterLevel(spawnedEntity, level);
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } catch (IllegalArgumentException e) {
            event.setCancelled(true);
        }
    }

    private Integer calculateMonsterLevel(Location location, RarityManager.Rarity rarity) {
        double distance = Math.sqrt(Math.pow(location.getX(), 2) + Math.pow(location.getZ(), 2));
        int level = (int) Math.ceil(distance / 200.0);

        level = adjustLevelBasedOnRarity(level, rarity);

        return level > 0 ? level : null;
    }

    private int adjustLevelBasedOnRarity(int level, RarityManager.Rarity rarity) {
        switch (rarity) {
            case ORDINARY:
                break;
            case RARE:
                level += 1;
                break;
            case EPIC:
                level += 2;
                break;
            case MYTHIC:
                level += 3;
                break;
            case LEGENDARY:
                level += 4;
                break;
            case DIVINE:
                level += 5;
                break;
            default:
                // Keine Anpassung für ORDINARY und andere nicht spezifizierte Rarities
                break;
        }
        return level;
    }

}
