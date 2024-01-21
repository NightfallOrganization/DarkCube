/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.manager.RarityManager;
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

    private RarityManager.Rarity getRarityForType(EntityType entityType) {
        switch (entityType) {
//            case ZOMBIE:
//            case ZOMBIE_VILLAGER:
//            case PILLAGER:
//            case STRAY:
//            case SKELETON:
//            case ENDERMAN:
//            case SPIDER:
//                return RarityManager.Rarity.RARE;
            case CREEPER:
                return RarityManager.Rarity.DIVINE;
            default:
                return RarityManager.Rarity.ORDINARY;
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getLocation().getWorld().equals(monsterWorld)) {
            EntityType type = event.getEntityType();
            if (isMonsterType(type)) {
                RarityManager.Rarity rarity = getRarityForType(type);
                Integer level = calculateMonsterLevel(event.getLocation(), rarity);
                if (level != null) {
                    monsterLevelManager.updateMonsterLevel((LivingEntity) event.getEntity(), level);
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private boolean isMonsterType(EntityType type) {
        return type.equals(EntityType.ZOMBIE) || type.equals(EntityType.ZOMBIE_VILLAGER) ||
                type.equals(EntityType.PILLAGER) || type.equals(EntityType.STRAY) ||
                type.equals(EntityType.SKELETON) || type.equals(EntityType.CREEPER) ||
                type.equals(EntityType.ENDERMAN) || type.equals(EntityType.SPIDER);
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
