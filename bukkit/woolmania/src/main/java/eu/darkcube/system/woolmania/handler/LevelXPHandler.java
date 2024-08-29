/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.handler;

import static eu.darkcube.system.woolmania.util.message.Message.LEVEL_UP;

import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Sounds;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.entity.Player;

public class LevelXPHandler {

    public void manageLevelXP(Player player) {
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        User user = UserAPI.instance().user(player.getUniqueId());
        int playerXP = woolManiaPlayer.getXP();
        int currentLevel = woolManiaPlayer.getLevel();
        int xpRequired = calculateXPRequiredForNextLevel(currentLevel);
        int privateBooster = WoolMania.getStaticPlayer(player).getPrivateBooster();
        int privateBoosterValue = (privateBooster <= 0) ? 1 : privateBooster;

        if (playerXP + privateBoosterValue > xpRequired) {
            int extantXP = playerXP - xpRequired;
            woolManiaPlayer.addLevel(1, player);
            woolManiaPlayer.setXP(extantXP + privateBoosterValue);
            Sounds.LEVEL_UP.playSound(player);
            user.sendMessage(LEVEL_UP,(currentLevel + 1));
        } else {
            woolManiaPlayer.addXP(privateBoosterValue);
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
