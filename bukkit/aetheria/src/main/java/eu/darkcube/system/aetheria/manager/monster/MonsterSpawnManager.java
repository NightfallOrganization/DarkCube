/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.manager.RarityManager;
import eu.darkcube.system.aetheria.manager.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

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

        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
            return;
        }

        Location location = event.getLocation();
        Integer level = calculateMonsterLevel(location);

        if (level != null) {
            RarityManager.Rarity rarity = selectRarityBasedOnLevel(level);
            monsterCreationManager.spawnMonsterAtLocation(rarity, location, level);
        }
    }

    private RarityManager.Rarity selectRarityBasedOnLevel(int level) {
        // Implementiere die Logik zur Auswahl der Seltenheit basierend auf dem Level
        // Beispiel: Höheres Level, höhere Chance auf seltenere Monster
        // Diese Methode muss entsprechend der gewünschten Logik angepasst werden
        return RarityManager.Rarity.ORDINARY; // Beispielrückgabe
    }

    private Integer calculateMonsterLevel(Location location) {
        double distance = Math.sqrt(Math.pow(location.getX(), 2) + Math.pow(location.getZ(), 2));
        int level = (int) Math.ceil(distance / 200.0);
        return level > 0 ? level : null;
    }

}
