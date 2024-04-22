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

public class RegenerationManager {
    private final JavaPlugin plugin;
    private final NamespacedKey regenerationKey;

    public RegenerationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.regenerationKey = new NamespacedKey(plugin, "virtual_regeneration");
    }

    public void setRegenerationRate(LivingEntity entity, double rate) {
        entity.getPersistentDataContainer().set(regenerationKey, PersistentDataType.DOUBLE, rate);
    }

    public double getRegenerationRate(LivingEntity entity) {
        return entity.getPersistentDataContainer().getOrDefault(regenerationKey, PersistentDataType.DOUBLE, 0.0);
    }
}
