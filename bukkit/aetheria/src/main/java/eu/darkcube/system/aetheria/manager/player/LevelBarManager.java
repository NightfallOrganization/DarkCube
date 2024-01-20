/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.player;

import eu.darkcube.system.aetheria.handler.LevelXPHandler;
import org.bukkit.entity.Player;

public class LevelBarManager {

    public static void updateExperienceBar(Player player, XPManager xpManager, LevelManager levelManager) {
        double currentXP = xpManager.getXP(player);
        int currentLevel = levelManager.getLevel(player);
        double xpRequired = LevelXPHandler.calculateXPRequiredForNextLevel(currentLevel);

        float xpFraction = (float) (currentXP / xpRequired);
        player.setExp(Math.min(xpFraction, 1.0f)); // Setzt den Fortschrittsbalken
    }

}