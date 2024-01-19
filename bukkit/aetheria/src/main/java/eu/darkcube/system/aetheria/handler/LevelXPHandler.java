/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.handler;

import eu.darkcube.system.aetheria.manager.player.LevelManager;
import eu.darkcube.system.aetheria.manager.player.XPManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.entity.Player;

public class LevelXPHandler {
    private final XPManager xpManager;
    private final LevelManager levelManager;

    public LevelXPHandler(XPManager xpManager, LevelManager levelManager) {
        this.xpManager = xpManager;
        this.levelManager = levelManager;
    }

    public void addXP(Player player, double xpToAdd) {
        double currentXP = xpManager.getXP(player);
        int currentLevel = levelManager.getLevel(player);
        double newXP = currentXP + xpToAdd;

        while (newXP >= calculateXPRequirement(currentLevel)) {
            newXP -= calculateXPRequirement(currentLevel);
            currentLevel++;
            levelManager.setLevel(player, currentLevel);
        }

        xpManager.setXP(player, newXP);
    }

    private double calculateXPRequirement(int level) {
        if (level == 0) return 20;
        return 20 + 480980 * Math.log10(level);
    }
}
