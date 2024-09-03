/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.player;

import static eu.darkcube.system.woolmania.util.message.Message.LEVEL_UP;

import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Sounds;
import eu.darkcube.system.woolmania.items.WoolItem;
import org.bukkit.entity.Player;

public class LevelXPHandler {

    public void manageLevelXP(Player player, WoolItem woolItem) {
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        User user = UserAPI.instance().user(player.getUniqueId());
        double playerXP = woolManiaPlayer.getXP();
        int currentLevel = woolManiaPlayer.getLevel();
        int xpRequired = calculateXPRequiredForNextLevel(currentLevel);
        int privateBooster = WoolMania.getStaticPlayer(player).getPrivateBooster();

        int tierXP = woolItem.getTier().getId();
        int privatBoosterXP = (privateBooster <= 0) ? 1 : privateBooster;
        int addXP = tierXP * privatBoosterXP;

        if (playerXP + addXP > xpRequired) {
            double finalXP = playerXP + addXP - xpRequired;
            woolManiaPlayer.addLevel(1, player);
            woolManiaPlayer.setXP(finalXP);
            Sounds.LEVEL_UP.playSound(player);
            user.sendMessage(LEVEL_UP,(currentLevel + 1));
            player.sendTitle("", "§7" + currentLevel + " §8» §b" + (currentLevel +1), 10, 70, 20);
        } else {
            woolManiaPlayer.addXP(addXP);
        }
    }

    public static int calculateXPRequiredForNextLevel(int currentLevel) {
        int baseXP = 50;
        int maxXP = 10000;
        int totalLevels = 99;

        int xpPerLevelIncrement = (maxXP - baseXP) / totalLevels;
        return xpPerLevelIncrement * (currentLevel - 1) + baseXP;
    }

}
