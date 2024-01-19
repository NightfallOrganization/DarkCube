/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class HealthManager {
    private final JavaPlugin plugin;
    private final NamespacedKey healthKey;

    public HealthManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.healthKey = new NamespacedKey(plugin, "virtual_health");
    }

    public void setHealth(LivingEntity entity, double health) {
        entity.getPersistentDataContainer().set(healthKey, PersistentDataType.DOUBLE, health);
    }

    public double getHealth(LivingEntity entity) {
        return entity.getPersistentDataContainer().getOrDefault(healthKey, PersistentDataType.DOUBLE, 0.0);
    }
}