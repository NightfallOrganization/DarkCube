package eu.darkcube.system.citybuild.commands;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CustomMonsterSpawn implements Listener {
    Random random = new Random();

    // Definieren Sie die Wahrscheinlichkeit, dass ein Monster überhaupt spawnen soll (z. B. 50%)
    private static final int SPAWN_CHANCE = 50;

    // Definieren Sie den Radius, innerhalb dessen nach vorhandenen Monstern gesucht wird
    private static final double CHECK_RADIUS = 10.0;

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        // Überprüfen Sie, ob das Monster in der Welt "Beastrealm" gespawnt wird
        if (event.getLocation().getWorld().getName().equals("Beastrealm")) {
            // Überprüfen Sie, ob das Monster auf natürliche Weise gespawnt wird
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                // Verhindern Sie das Spawnen des Monsters
                event.setCancelled(true);

                // Überprüfen Sie, ob sich bereits Monster in der Nähe befinden
                List<Entity> nearbyEntities = event.getLocation().getWorld()
                        .getNearbyEntities(event.getLocation(), CHECK_RADIUS, CHECK_RADIUS, CHECK_RADIUS)
                        .stream()
                        .filter(entity -> entity instanceof Monster)
                        .collect(Collectors.toList());

                if (nearbyEntities.isEmpty()) {
                    // Überprüfen Sie, ob ein Monster überhaupt spawnen soll
                    if (random.nextInt(100) < SPAWN_CHANCE) {
                        // Generieren Sie eine Zufallszahl zwischen 1 und 100
                        int randomNumber = random.nextInt(100) + 1;

                        if (randomNumber <= 5) {
                            spawnEntity(event.getLocation(), Creeper.class);
                        } else if (randomNumber <= 6) {
                            spawnEntity(event.getLocation(), Ravager.class);
                        } else if (randomNumber <= 16) {
                            spawnEntity(event.getLocation(), Pillager.class);
                        } else if (randomNumber <= 21) {
                            spawnEntity(event.getLocation(), Spider.class);
                        } else if (randomNumber <= 23) {
                            spawnEntity(event.getLocation(), Stray.class);
                        } else if (randomNumber <= 70) {
                            spawnEntity(event.getLocation(), Zombie.class);
                        } else if (randomNumber <= 100) {
                            spawnEntity(event.getLocation(), Skeleton.class);
                        }
                    }
                }
            }
        }
    }

    private void spawnEntity(Location location, Class<? extends LivingEntity> entityClass) {
        location.getWorld().spawn(location, entityClass);
    }
}
