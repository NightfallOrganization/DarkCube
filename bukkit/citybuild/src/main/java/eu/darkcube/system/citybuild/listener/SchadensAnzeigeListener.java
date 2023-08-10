/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import eu.darkcube.system.citybuild.util.CustomHealthManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SchadensAnzeigeListener implements Listener {

    private final Random random = new Random();
    private Map<UUID, Integer> previousHealthMap = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            CustomHealthManager healthManager = Citybuild.getInstance().getHealthManager();
            previousHealthMap.put(entity.getUniqueId(), healthManager.getMonsterHealth((LivingEntity) entity));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player && entity instanceof LivingEntity) {
            Player player = (Player) damager;

            boolean isCritical = player.getFallDistance() > 0.2 && !player.isOnGround();

            if (previousHealthMap.containsKey(entity.getUniqueId())) {
                int previousHealth = previousHealthMap.get(entity.getUniqueId());
                Bukkit.getScheduler().runTaskLater(Citybuild.getInstance(), () -> {
                    CustomHealthManager healthManager = Citybuild.getInstance().getHealthManager();
                    int newHealth = healthManager.getMonsterHealth((LivingEntity) entity);
                    int schaden = previousHealth - newHealth;

                    zeigeSchadenswert(entity.getLocation(), schaden, isCritical);
                    previousHealthMap.remove(entity.getUniqueId());
                }, 1L);
            }
        }
    }

    private void zeigeSchadenswert(Location loc, int schaden, boolean isCritical) {
        double xOffset = (random.nextDouble() * 2 - 1) * 0.5;
        double zOffset = (random.nextDouble() * 2 - 1) * 0.5;

        loc.add(xOffset, 1.0, zOffset);

        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCustomNameVisible(true);
            if (isCritical) {
                armorStand.setCustomName("§f✧§e" + schaden + "§c✧");
            } else {
                armorStand.setCustomName("§c-" + schaden);
            }
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);
        });

        Bukkit.getScheduler().runTaskLater(Citybuild.getInstance(), as::remove, 10L);
    }
}
