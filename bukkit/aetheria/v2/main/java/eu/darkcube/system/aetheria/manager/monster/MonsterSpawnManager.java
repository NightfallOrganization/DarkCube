/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.manager.RarityManager;
import eu.darkcube.system.aetheria.manager.WorldManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Optional;
import java.util.Random;

public class MonsterSpawnManager implements Listener {
    private static final NamespacedKey MONSTER_TYPE = new NamespacedKey(Aetheria.getInstance(), "monster_type");
    private final MonsterCreationManager monsterCreationManager;
    private MonsterLevelManager monsterLevelManager;
    private final Random random = new Random();

    public MonsterSpawnManager(MonsterCreationManager monsterCreationManager, MonsterLevelManager monsterLevelManager) {
        this.monsterCreationManager = monsterCreationManager;
        this.monsterLevelManager = monsterLevelManager;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!event.getEntity().getPersistentDataContainer().has(MONSTER_TYPE, MonsterCreationManager.MonsterType.MONSTER_TYPE)) {
            // Alles, was nicht richtig eingestellt ist, darf nicht spawnen
            event.setCancelled(true);
        }
    }

    public void spawnMonster(World world, Location location, EntityTypeManager.EntityType entityType, RarityManager.Rarity rarity) {
        if (shouldSpawnBasedOnRarity(rarity)) {
            Optional<MonsterCreationManager.MonsterType> monsterTypeOptional = monsterCreationManager.getMonsterTypeByEntityType(entityType);
            if (monsterTypeOptional.isEmpty()) throw new IllegalArgumentException("Type cannot be spawned - it does not exist as a monster?");
            MonsterCreationManager.MonsterType monsterType = monsterTypeOptional.get();
            Integer level = calculateMonsterLevel(location, rarity);

            // Prüfe, ob das Monsterlevel im erlaubten Bereich liegt.
            if (level == null || level < monsterType.getMinLevel() || level > monsterType.getMaxLevel()) {
                return; // Nicht spawnen, wenn das Level außerhalb des Bereichs liegt.
            }

            Class<? extends Entity> bukkitEntityClass = monsterType.getEntityType().getEntityClass();
            Location spawnLocation = location.clone().add(0, 1, 0); // Erhöhe die Y-Koordinate um 1.

            world.spawn(spawnLocation, bukkitEntityClass, entity -> {
                entity.getPersistentDataContainer().set(MONSTER_TYPE, MonsterCreationManager.MonsterType.MONSTER_TYPE, monsterType);
                entity.customName(Component.text(monsterType.getName()));
                entity.setCustomNameVisible(true);
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    monsterLevelManager.updateMonsterLevel(livingEntity, level);
                }
            });
        }
    }

    private boolean shouldSpawnBasedOnRarity(RarityManager.Rarity rarity) {
        int chance = switch (rarity) {
            case ORDINARY -> 1; // Immer spawnen
            case RARE -> 5;
            case EPIC -> 25;
            case MYTHIC -> 50;
            case LEGENDARY -> 500;
            case DIVINE -> 2500;
            case UNIQUE -> Integer.MAX_VALUE; // Niemals spawnen
        };

        if (chance == 1) return true; // ORDINARY immer spawnen lassen
        if (chance == Integer.MAX_VALUE) return false; // UNIQUE niemals spawnen lassen
        return this.random.nextInt(chance) == 0; // Generiere eine Zufallszahl und prüfe, ob sie 0 ist
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