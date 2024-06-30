/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class CorManager {

    private final NamespacedKey corKey;
    private Plugin plugin;

    public CorManager(Plugin plugin) {
        this.plugin = plugin;
        this.corKey = new NamespacedKey(plugin, "cor");
    }

    public void setCor(Player player, int cor) {
        player.getPersistentDataContainer().set(corKey, PersistentDataType.INTEGER, cor);
    }

    public int getCor(Player player) {
        return player.getPersistentDataContainer().getOrDefault(corKey, PersistentDataType.INTEGER, 0);
    }
}
