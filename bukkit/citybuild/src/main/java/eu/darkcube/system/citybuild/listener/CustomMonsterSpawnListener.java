/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class CustomMonsterSpawnListener implements Listener {
    private Random random = new Random();
    private static final int SPAWN_CHANCE = 5;
    private static final double CHECK_RADIUS = 15.0;

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getLocation().getWorld().getName().equals("Beastrealm") && event.getSpawnReason() == SpawnReason.NATURAL) {
            event.setCancelled(true);
            List<Entity> nearbyEntities = event.getLocation().getWorld().getNearbyEntities(event.getLocation(), CHECK_RADIUS, CHECK_RADIUS, CHECK_RADIUS)
                    .stream()
                    .filter(entity -> entity instanceof Monster)
                    .collect(Collectors.toList());

            if (nearbyEntities.isEmpty() && this.random.nextInt(100) < SPAWN_CHANCE) {
                int randomNumber = this.random.nextInt(100) + 1;
                if (randomNumber <= 6) {
                    this.spawnEntity(event.getLocation(), Creeper.class);
                } else if (randomNumber <= 13) {
                    this.spawnEntity(event.getLocation(), Spider.class);
                } else if (randomNumber <= 25) {
                    this.spawnEntity(event.getLocation(), Skeleton.class);
                } else if (randomNumber <= 27) {
                    this.spawnEntity(event.getLocation(), Stray.class);
                } else if (randomNumber <= 35) {
                    this.spawnEntity(event.getLocation(), Enderman.class);
                } else {
                    this.spawnEntity(event.getLocation(), Zombie.class);
                }
            }
        }
    }

    private void spawnEntity(Location location, Class<? extends LivingEntity> entityClass) {
        location.getWorld().spawn(location, entityClass);
    }
}
