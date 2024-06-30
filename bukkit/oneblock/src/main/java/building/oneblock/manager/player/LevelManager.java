/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelManager {
    private final JavaPlugin plugin;
    private final NamespacedKey levelKey;

    public LevelManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.levelKey = new NamespacedKey(plugin, "virtual_level");
    }

    public void setLevel(LivingEntity entity, int level) {
        entity.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
    }

    public int getLevel(LivingEntity entity) {
        return entity.getPersistentDataContainer().getOrDefault(levelKey, PersistentDataType.INTEGER, 0);
    }
}
