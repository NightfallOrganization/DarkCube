/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Spectator {

    public static void setPlayerToSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }
}
