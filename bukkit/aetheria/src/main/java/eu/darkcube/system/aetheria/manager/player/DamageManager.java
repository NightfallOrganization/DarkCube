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

public class DamageManager {
    private final JavaPlugin plugin;
    private final NamespacedKey damageKey;

    public DamageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.damageKey = new NamespacedKey(plugin, "virtual_damage");
    }

    public void setDamage(LivingEntity entity, double damage) {
        entity.getPersistentDataContainer().set(damageKey, PersistentDataType.DOUBLE, damage);
    }

    public double getDamage(LivingEntity entity) {
        return entity.getPersistentDataContainer().getOrDefault(damageKey, PersistentDataType.DOUBLE, 0.0);
    }
}
