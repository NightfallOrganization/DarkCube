/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class MaxHealthManager {
    private final JavaPlugin plugin;
    private final NamespacedKey maxHealthKey;

    public MaxHealthManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.maxHealthKey = new NamespacedKey(plugin, "virtual_max_health");
    }

    public void setMaxHealth(LivingEntity entity, double maxHealth) {
        entity.getPersistentDataContainer().set(maxHealthKey, PersistentDataType.DOUBLE, maxHealth);
    }

    public double getMaxHealth(LivingEntity entity) {
        return entity.getPersistentDataContainer().getOrDefault(maxHealthKey, PersistentDataType.DOUBLE, 0.0);
    }
}
