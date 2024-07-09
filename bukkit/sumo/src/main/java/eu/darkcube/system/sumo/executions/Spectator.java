/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.executions;

import eu.darkcube.system.sumo.manager.MapManager;
import eu.darkcube.system.sumo.prefix.PrefixManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;

public class Spectator {

    private static MapManager mainRuler;
    private static PrefixManager prefixManager;

    public static void setPrefixManager(PrefixManager prefixManager) {
        Spectator.prefixManager = prefixManager;
    }

    public static void setMainRuler(MapManager mainRuler) {
        Spectator.mainRuler = mainRuler;
    }

    public static void setPlayerToSpectator(Player player) {
        World activeWorld = mainRuler.getActiveWorld();

        if (activeWorld != null) {
            player.setGameMode(GameMode.SPECTATOR);

            prefixManager.setupPlayer(player);
            prefixManager.setupOtherPlayers(player);

            Location targetLocation = new Location(activeWorld, 0.5, 110, 0.5);
            player.teleport(targetLocation);
        } else {
            player.sendMessage("§7Aktive Welt: §bKeine");
        }
    }

}
