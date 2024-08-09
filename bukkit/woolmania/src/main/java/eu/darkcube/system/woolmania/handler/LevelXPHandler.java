/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.handler;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LevelXPHandler {

    public void manageLevelXP(Player player) {
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        int xp = woolManiaPlayer.getXP();
        int currentLevel = woolManiaPlayer.getLevel();
        int xpRequired = calculateXPRequiredForNextLevel(currentLevel);
        woolManiaPlayer.addXP(1);

        if (xp >= xpRequired) {
            woolManiaPlayer.addLevel(1);
            woolManiaPlayer.setXP(0);

            player.sendMessage("§7Du hast Level §b" + (currentLevel + 1) + " §7erreicht!");
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 50f, 1f);
        }
    }

    public static int calculateXPRequiredForNextLevel(int currentLevel) {
        int baseXP = 20;
        int maxXP = 43000;
        int totalLevels = 99;

        int xpPerLevelIncrement = (maxXP - baseXP) / totalLevels;
        return xpPerLevelIncrement * (currentLevel - 1) + baseXP;
    }

}
