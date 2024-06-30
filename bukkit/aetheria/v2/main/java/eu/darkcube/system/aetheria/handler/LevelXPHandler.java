/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.handler;

import eu.darkcube.system.aetheria.manager.shared.LevelManager;
import eu.darkcube.system.aetheria.manager.player.XPManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LevelXPHandler {

    public static void checkLevelUp(Player player, XPManager xpManager, LevelManager levelManager) {
        double xp = xpManager.getXP(player);
        int currentLevel = levelManager.getLevel(player);
        double xpRequired = calculateXPRequiredForNextLevel(currentLevel);

        if (xp >= xpRequired) {
            levelManager.setLevel(player, currentLevel + 1);
            xpManager.setXP(player, xp - xpRequired); // Reset XP to 0 after leveling up
            // Optional: Notify the player about the level up
            player.sendMessage("§7Du hast Level §e" + (currentLevel + 1) + " §7erreicht!");
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 5f, 1f);
        }
    }

    public static double calculateXPRequiredForNextLevel(int currentLevel) {
        double baseXP = 20;
        double maxXP = 43000;
        int totalLevels = 99;

        double xpPerLevelIncrement = (maxXP - baseXP) / totalLevels;
        return xpPerLevelIncrement * (currentLevel - 1) + baseXP;
    }

}
