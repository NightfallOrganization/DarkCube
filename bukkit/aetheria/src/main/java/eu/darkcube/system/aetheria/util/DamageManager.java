/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class DamageManager implements Listener {
    private static final double DEFAULT_DAMAGE = 1.0;
    private final NamespacedKey DAMAGE_KEY;
    private final NamespacedKey DAMAGE_MAP_KEY;
    private final Aetheria aetheria;

    public DamageManager(Aetheria aetheria) {
        this.aetheria = aetheria;
        this.DAMAGE_MAP_KEY = new NamespacedKey(aetheria, "damage_map");
        this.DAMAGE_KEY = new NamespacedKey(aetheria, "damage");
    }

    public void addDamage(Player player, int damageToAdd) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        double currentDamage = getDamage(player);
        data.set(DAMAGE_KEY, PersistentDataType.DOUBLE, currentDamage + damageToAdd);
    }

    public void resetDamage(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(DAMAGE_KEY, PersistentDataType.DOUBLE, DEFAULT_DAMAGE);
    }

    public double getDamage(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.getOrDefault(DAMAGE_KEY, PersistentDataType.DOUBLE, DEFAULT_DAMAGE);
    }

    public boolean mayDamage(Entity damager, Entity target) {
        return true;
    }

    private double baseDamage(Entity entity, int entityLevel) {
        double baseDamage;
        if (entity instanceof Player player) {
            baseDamage = getDamage(player);
        } else {
            baseDamage = entityLevel * 2;
        }
        return baseDamage;
    }

    private double itemDamage(ItemStack item) {
        if (!item.hasItemMeta()) return 0;
        return aetheria.customSwordManager().getSwordAttackDamage(item);
    }

    private double additionalDamage(Entity entity) {
        double additionalDamage = 0;
        if (entity instanceof HumanEntity humanEntity) {
            PlayerInventory inventory = humanEntity.getInventory();
            ItemStack mainHand = inventory.getItemInMainHand();
            additionalDamage += itemDamage(mainHand);
        }
        return additionalDamage;
    }

    private double applyModifiers(Entity entity, Entity target, double damage) {
        double multiplier = 1;
        if (entity instanceof Player player) {
            if (!player.isInsideVehicle() && !player.isOnGround() && !player.isSprinting() && player.getFallDistance() > 0 && player.getAttackCooldown() == 1.0F) {
                multiplier *= 1.25;
                target.setMetadata("criticalDamage", new FixedMetadataValue(aetheria, true));
            }
        }
        return damage * multiplier;
    }

    private double calculateDamage(Entity entity, Entity target, int entityLevel) {
        double baseDamage = baseDamage(entity, entityLevel);
        double additionalDamage = additionalDamage(entity);
        double damage = baseDamage + additionalDamage;
        damage = applyModifiers(entity, target, damage);
        return damage;
    }

    private double randomDamageVariation() {
        return 1 + (Math.random() * 0.1 - 0.05);
    }

    private boolean hasDamageMap(Entity entity) {
        return entity.getPersistentDataContainer().has(DAMAGE_MAP_KEY);
    }

    private DamageMap damageMap(Entity entity) {
        if (!hasDamageMap(entity)) return new DamageMap();
        return entity.getPersistentDataContainer().get(DAMAGE_MAP_KEY, DamageMap.TYPE);
    }

    private void damageMap(Entity entity, DamageMap damageMap) {
        entity.getPersistentDataContainer().set(DAMAGE_MAP_KEY, DamageMap.TYPE, damageMap);
    }

    private void damage(Entity target, Entity attacker, int damage, boolean critical) {
        int health = aetheria.getHealthManager().getHealth(target) - damage;
        if (health < 0) health = 0;
        aetheria.getHealthManager().setHealth(target, health);
        aetheria.schadensAnzeige().zeigeSchadenswert(target.getLocation(), attacker, damage, critical);

        if (health == 0) {
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.setHealth(0);
            } else {
                target.remove();
            }
        } else {
            aetheria.monsterStatsManager().updateMonsterName(target);
        }
    }

    @EventHandler(priority = EventPriority.HIGH) public void handle(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        boolean critical = entity.hasMetadata("criticalDamage");
        entity.removeMetadata("criticalDamage", aetheria);
        Entity attacker = event instanceof EntityDamageByEntityEvent e ? e.getDamager() : null;
        damage(entity, attacker, (int) Math.ceil(event.getDamage()), critical);
        event.setDamage(0);
    }

    @EventHandler public void handle(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        int damagerLevel = aetheria.monsterStatsManager().level(damager);
        if (damager instanceof Player player) damagerLevel = aetheria.levelXPManager().getLevel(player);

        if (damagerLevel == -1) return; // Entity does not have a level -> Invalid entity

        Entity target = event.getEntity();

        if (!mayDamage(damager, target)) {
            event.setCancelled(true);
            return;
        }

        double damage = calculateDamage(damager, target, damagerLevel);

        damage = damage * randomDamageVariation();

        if (damager instanceof Player) {
            DamageMap damageMap = damageMap(target);
            damageMap.add(damager.getUniqueId(), damage);
            damageMap(target, damageMap);
        }

        event.setDamage(damage);
    }

    @EventHandler public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Monster)) {
            return;
        }

        if (!hasDamageMap(entity)) return;

        // Holt die Schadensmap für das aktuelle Monster
        DamageMap damageMap = damageMap(entity);
        // Ermittelt den Spieler, der dem Monster den meisten Schaden zugefügt hat
        UUID topDamager = damageMap.topDamager();

        if (topDamager != null) {
            int level = aetheria.monsterStatsManager().level(entity);
            double xp = aetheria.levelXPManager().getXPForLevel(level);
            double xpMultiplier = 0.005;
            xp *= xpMultiplier;

            Player topDamagerPlayer = Bukkit.getPlayer(topDamager);
            if (topDamagerPlayer != null) {
                aetheria.levelXPManager().addXP(topDamagerPlayer, xp);

                // XP-Wert anzeigen
                aetheria.schadensAnzeige().zeigeXPWert(entity.getLocation(), null, xp);
            }
        }
    }

    @EventHandler public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.CUSTOM) {
            event.setCancelled(true);
        }
    }
}
