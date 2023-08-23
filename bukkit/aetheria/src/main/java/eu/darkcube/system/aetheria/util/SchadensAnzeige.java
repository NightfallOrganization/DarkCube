/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.Random;

public class SchadensAnzeige implements Listener {
    private final Random random = new Random();

    public SchadensAnzeige() {
    }
//    private Map<UUID, Integer> previousHealthMap = new HashMap<>();
//
//    @EventHandler(priority = EventPriority.LOW) public void onEntityDamage(EntityDamageEvent event) {
//        Entity entity = event.getEntity();
//        if (entity instanceof LivingEntity) {
//            CustomHealthManager healthManager = Aetheria.getInstance().getHealthManager();
//            previousHealthMap.put(entity.getUniqueId(), healthManager.getHealth(entity));
//        }
//    }
//
//    @EventHandler public void onDamage(EntityDamageByEntityEvent event) {
//        Entity damager = event.getDamager();
//        Entity entity = event.getEntity();
//
//        if (damager instanceof Player player && entity instanceof LivingEntity) {
//
//            boolean isWithinCooldownRange = player.getAttackCooldown() >= 0.90F && player.getAttackCooldown() <= 1.10F;
//            boolean isCritical = isWithinCooldownRange && !player.isInsideVehicle() && !player.isOnGround() && !player.isSprinting() && player.getFallDistance() > 0;
//
//            if (previousHealthMap.containsKey(entity.getUniqueId())) {
//                int previousHealth = previousHealthMap.get(entity.getUniqueId());
//
//                Bukkit.getScheduler().runTaskLater(Aetheria.getInstance(), () -> {
//                    CustomHealthManager healthManager = Aetheria.getInstance().getHealthManager();
//                    int newHealth = healthManager.getHealth(entity);
//                    int schaden = previousHealth - newHealth;
//
//                    // Überprüfen, ob dieser Schaden ausreicht, um das Monster zu töten
//                    if (previousHealth - event.getFinalDamage() > 0) {
//                        zeigeSchadenswert(entity.getLocation(), schaden, isCritical);
//                    }
//
//                    previousHealthMap.remove(entity.getUniqueId());
//                }, 1L);
//            }
//        }
//    }

    public void zeigeXPWert(Location loc, @Nullable Entity attacker, double xp) {
        modifyLocation(loc, attacker);

        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(formatXPValue(xp));
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);
        });

        // Hier wird der Sound abgespielt
        loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);

        Bukkit.getScheduler().runTaskLater(Aetheria.getInstance(), as::remove, 25L);
    }

    private String formatXPValue(double value) {
        // Dieser Soundaufruf wurde entfernt, weil er hier nicht benötigt wird
        if (value == 0) {
            return "+ XP";
        } else if (value < 1) {
            String formattedValue = String.format("%.3f", value).replaceAll("0+$", "");
            if (formattedValue.endsWith(".")) {
                formattedValue = formattedValue.substring(0, formattedValue.length() - 1);
            }
            return "§a+" + formattedValue + " XP";
        } else {
            return "§a+" + value + " XP";
        }
    }

    private void modifyLocation(Location location, @Nullable Entity attacker) {
        if (attacker != null) {
            Vector dir = attacker.getLocation().toVector().subtract(location.toVector()).normalize();
            Vector offset = dir.getCrossProduct(new Vector(0, 1, 0));
            offset.multiply(Math.random() * -0.5).multiply(0.8);
            offset.add(dir.multiply(0.3));
            offset.setY(offset.getY() + 1.1);
            if (offset.length() == 0 || Double.isInfinite(offset.length())) {
                attacker = null;
            } else {
                location.add(offset);
            }
        }
        if (attacker == null) {
            double xOffset = random.nextDouble() - 0.5;
            double zOffset = random.nextDouble() - 0.5;
            double yOffset = random.nextDouble();

            location.add(xOffset, 0.5 + yOffset, zOffset);
        }
    }

    public void zeigeSchadenswert(Location loc, @Nullable Entity attacker, int schaden, boolean isCritical) {
        modifyLocation(loc, attacker);

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

        Bukkit.getScheduler().runTaskLater(Aetheria.getInstance(), as::remove, 15L);
    }
}
