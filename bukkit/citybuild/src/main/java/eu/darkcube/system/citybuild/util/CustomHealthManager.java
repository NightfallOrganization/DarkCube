/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomHealthManager {

    private final NamespacedKey healthKey;
    private final NamespacedKey maxHealthKey;
    private final NamespacedKey regenKey;
    private final NamespacedKey aroundDamageKey;
    private final JavaPlugin plugin;

    public CustomHealthManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.healthKey = new NamespacedKey(plugin, "health");
        this.maxHealthKey = new NamespacedKey(plugin, "max_health");
        this.regenKey = new NamespacedKey(plugin, "regeneration");
        this.aroundDamageKey = new NamespacedKey(plugin, "AroundDamage");// 25
        startRegenerationTask();
    }

    private void startRegenerationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    int currentHealth = getHealth(player);
                    int maxHealth = getMaxHealth(player);
                    if (currentHealth < maxHealth) {
                        int regen = getRegen(player);
                        setHealth(player, Math.min(currentHealth + regen, maxHealth));
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0L, 100L);  // 100 ticks = 5 seconds
    }

    public void setHealth(Player player, int health) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(healthKey, PersistentDataType.INTEGER, health);
    }

    public int getHealth(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(healthKey, PersistentDataType.INTEGER)) {
            return data.get(healthKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public void resetRegen(Player player) {
        setRegen(player, 1);
    }

    public void resetHealth(Player player) {
        setHealth(player, 20);  // Setzt die Gesundheit auf den Standardwert von 20
    }

    public void resetMaxHealth(Player player) {
        setMaxHealth(player, 20);  // Hier setzen wir das Maximum Health auf den Standardwert 20
    }

    public void setMaxHealth(Player player, int maxHealth) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(maxHealthKey, PersistentDataType.INTEGER, maxHealth);
    }

    public void addMaxHealth(Player player, int healthToAdd) {
        int currentMaxHealth = getMaxHealth(player);
        setMaxHealth(player, currentMaxHealth + healthToAdd);
    }

    public int getMaxHealth(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(maxHealthKey, PersistentDataType.INTEGER)) {
            return data.get(maxHealthKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public void setRegen(Player player, int regen) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(regenKey, PersistentDataType.INTEGER, regen);
    }

    public void addRegen(Player player, int regenToAdd) {
        int currentRegen = getRegen(player);
        setRegen(player, currentRegen + regenToAdd);
    }

    public int getRegen(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(regenKey, PersistentDataType.INTEGER)) {
            return data.get(regenKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public void resetAroundDamage(Player player) {
        this.setAroundDamage(player, 0.0);
    }

    public void setAroundDamage(Player player, double value) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (!data.has(this.aroundDamageKey, PersistentDataType.DOUBLE)) {
            data.set(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);
        }

        data.set(this.aroundDamageKey, PersistentDataType.DOUBLE, value);
    }

    public void setMonsterHealth(LivingEntity monster, int health) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        data.set(healthKey, PersistentDataType.INTEGER, health);
    }

    public void setMonsterMaxHealth(LivingEntity monster, int health) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        data.set(maxHealthKey, PersistentDataType.INTEGER, health);
    }

    public int getMonsterHealth(LivingEntity monster) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        if (data.has(healthKey, PersistentDataType.INTEGER)) {
            return data.get(healthKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public int getMonsterMaxHealth(LivingEntity monster) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        if (data.has(maxHealthKey, PersistentDataType.INTEGER)) {
            return data.get(maxHealthKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

}
