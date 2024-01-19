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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomHealthManager {

    private final NamespacedKey healthKey;
    private final NamespacedKey maxHealthKey;
    private final NamespacedKey regenKey;
    private final NamespacedKey aroundDamageKey;
    private final Aetheria plugin;

    public CustomHealthManager(Aetheria plugin) {
        this.plugin = plugin;
        this.healthKey = new NamespacedKey(plugin, "health");
        this.maxHealthKey = new NamespacedKey(plugin, "max_health");
        this.regenKey = new NamespacedKey(plugin, "regeneration");
        this.aroundDamageKey = new NamespacedKey(plugin, "AroundDamage");// 25
        startRegenerationTask();
    }

    private void startRegenerationTask() {
        new BukkitRunnable() {
            @Override public void run() {
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

    public void resetRegen(Entity player) {
        setRegen(player, 1);
    }

    public void resetHealth(Entity player) {
        setHealth(player, 20);  // Setzt die Gesundheit auf den Standardwert von 20
    }

    public void resetMaxHealth(Entity player) {
        setMaxHealth(player, 20);  // Hier setzen wir das Maximum Health auf den Standardwert 20
    }

    public void addMaxHealth(Entity player, int healthToAdd) {
        int currentMaxHealth = getMaxHealth(player);
        setMaxHealth(player, currentMaxHealth + healthToAdd);
    }

    public void setRegen(Entity player, int regen) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(regenKey, PersistentDataType.INTEGER, regen);
    }

    public void addRegen(Entity player, int regenToAdd) {
        int currentRegen = getRegen(player);
        setRegen(player, currentRegen + regenToAdd);
    }

    public int getRegen(Entity player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(regenKey, PersistentDataType.INTEGER)) {
            return data.get(regenKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public void resetAroundDamage(Entity player) {
        this.setAroundDamage(player, 0.0);
    }

    public void setAroundDamage(Entity player, double value) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (!data.has(this.aroundDamageKey, PersistentDataType.DOUBLE)) {
            data.set(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);
        }

        data.set(this.aroundDamageKey, PersistentDataType.DOUBLE, value);
    }

    public void setHealth(Entity monster, int health) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        data.set(healthKey, PersistentDataType.INTEGER, health);
    }

    public void setMaxHealth(Entity monster, int health) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        data.set(maxHealthKey, PersistentDataType.INTEGER, health);
    }

    public int getHealth(Entity monster) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        if (data.has(healthKey, PersistentDataType.INTEGER)) {
            return data.get(healthKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public int getMaxHealth(Entity monster) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        if (data.has(maxHealthKey, PersistentDataType.INTEGER)) {
            return data.get(maxHealthKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

}
