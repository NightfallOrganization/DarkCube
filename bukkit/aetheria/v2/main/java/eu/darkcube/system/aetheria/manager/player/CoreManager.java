/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreManager {
    private final JavaPlugin plugin;
    private final NamespacedKey coreKey;

    public CoreManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.coreKey = new NamespacedKey(plugin, "virtual_core");
    }

    public void setCoreValue(Player player, int value) {
        player.getPersistentDataContainer().set(coreKey, PersistentDataType.INTEGER, value);
    }

    public int getCoreValue(Player player) {
        return player.getPersistentDataContainer().getOrDefault(coreKey, PersistentDataType.INTEGER, 0);
    }
}
