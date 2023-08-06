//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class CustomMonsterSpawn implements Listener {
    Random random = new Random();
    private static final int SPAWN_CHANCE = 30;
    private static final double CHECK_RADIUS = 15.0;

    public CustomMonsterSpawn() {
    }// 14

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getLocation().getWorld().getName().equals("Beastrealm") && event.getSpawnReason() == SpawnReason.NATURAL) {// 21 22
            event.setCancelled(true);// 23
            List<Entity> nearbyEntities = (List)event.getLocation().getWorld().getNearbyEntities(event.getLocation(), 15.0, 15.0, 15.0).stream().filter((entity) -> {// 25 26 27 28
                return entity instanceof Monster;
            }).collect(Collectors.toList());// 29
            if (nearbyEntities.isEmpty() && this.random.nextInt(100) < 30) {// 31 32
                int randomNumber = this.random.nextInt(100) + 1;// 33
                if (randomNumber <= 6) {// 35
                    this.spawnEntity(event.getLocation(), Creeper.class);// 36
                } else if (randomNumber <= 13) {// 37
                    this.spawnEntity(event.getLocation(), Spider.class);// 38
                } else if (randomNumber <= 25) {// 39
                    this.spawnEntity(event.getLocation(), Skeleton.class);// 40
                } else if (randomNumber <= 27) {// 41
                    this.spawnEntity(event.getLocation(), Stray.class);// 42
                } else if (randomNumber <= 35) {// 43
                    this.spawnEntity(event.getLocation(), Enderman.class);// 44
                } else if (randomNumber <= 100) {// 45
                    this.spawnEntity(event.getLocation(), Zombie.class);// 46
                }
            }
        }

    }// 52

    private void spawnEntity(Location location, Class<? extends LivingEntity> entityClass) {
        location.getWorld().spawn(location, entityClass);// 55
    }// 56
}
