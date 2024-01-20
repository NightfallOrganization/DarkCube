/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.player;

import eu.darkcube.system.aetheria.handler.LevelXPHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class XPManager {
    private final JavaPlugin plugin;
    private final NamespacedKey xpKey;
    private final LevelManager levelManager;

    public XPManager(JavaPlugin plugin, LevelManager levelManager) {
        this.plugin = plugin;
        this.xpKey = new NamespacedKey(plugin, "virtual_xp");
        this.levelManager = levelManager;
    }

    public void setXP(Player player, double xp) {
        player.getPersistentDataContainer().set(xpKey, PersistentDataType.DOUBLE, xp);
        LevelXPHandler.checkLevelUp(player, this, levelManager);
    }

    public double getXP(Player player) {
        return player.getPersistentDataContainer().getOrDefault(xpKey, PersistentDataType.DOUBLE, 0.0);
    }

}
