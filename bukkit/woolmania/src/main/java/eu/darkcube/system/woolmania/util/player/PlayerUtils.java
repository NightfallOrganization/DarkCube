/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.player;

import static eu.darkcube.system.woolmania.enums.Abilitys.FLY;
import static eu.darkcube.system.woolmania.enums.Abilitys.SPEED;

import eu.darkcube.system.woolmania.WoolMania;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static void updateExperienceBar(Player player) {
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        int level = woolManiaPlayer.getLevel();
        double currentXP = woolManiaPlayer.getXP();
        double currentMaxXP = LevelXPHandler.calculateXPRequiredForNextLevel(level);

        float manaFraction = (float) (currentXP / currentMaxXP);
        player.setExp(Math.min(manaFraction, 0.9999f));
    }

    public static void updateAbilitys(Player player) {
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        AttributeInstance speedAttribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if (woolManiaPlayer.isActiveAbility(FLY)) {
            player.setAllowFlight(true);
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        if (woolManiaPlayer.isActiveAbility(SPEED)) {
            speedAttribute.setBaseValue(0.3);
        } else {
            speedAttribute.setBaseValue(0.1);
        }

    }

}
