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
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Optional;

public class MonsterSpawnManager implements Listener {
    private static final NamespacedKey MONSTER_TYPE = new NamespacedKey(Aetheria.getInstance(), "monster_type");
    private final MonsterCreationManager monsterCreationManager;

    public MonsterSpawnManager(MonsterCreationManager monsterCreationManager) {
        this.monsterCreationManager = monsterCreationManager;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!event.getEntity().getPersistentDataContainer().has(MONSTER_TYPE, MonsterCreationManager.MonsterType.MONSTER_TYPE)) {
            // Alles, was nicht richtig eingestellt ist, darf nicht spawnen
            event.setCancelled(true);
        }
    }

    public void spawnMonster(World world, Location location, EntityTypeManager.EntityType entityType) {
        var monsterTypeOptional = monsterCreationManager.getMonsterTypeByEntityType(entityType);
        if (monsterTypeOptional.isEmpty()) throw new IllegalArgumentException("Type can not be spawned - it does not exist as a monster?");
        var monsterType = monsterTypeOptional.get();
        var bukkitEntityClass = monsterType.getEntityType().getEntityClass();
        world.spawn(location, bukkitEntityClass, entity -> {
            // setup monster. Name/PersistentData, etc.
            // Das hier wird ausgeführt, BEVOR das monster in der Welt ist.
            // Teleport/Bewegung, etc ist hier nicht erlaubt.

            entity.getPersistentDataContainer().set(MONSTER_TYPE, MonsterCreationManager.MonsterType.MONSTER_TYPE, monsterType);
            entity.customName(Component.text(monsterType.getName()));
        });
    }
}

class OldCode implements Listener {

    private World monsterWorld;
    private MonsterLevelManager monsterLevelManager;
    private MonsterCreationManager monsterCreationManager;

    public OldCode(MonsterLevelManager monsterLevelManager, MonsterCreationManager monsterCreationManager) {
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
            EntityTypeManager.EntityType entityType = EntityTypeManager.EntityType.valueOf(spawnedEntity.getType());
            Optional<MonsterCreationManager.MonsterType> monsterOpt = monsterCreationManager.getMonsterTypeByEntityType(entityType);

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
