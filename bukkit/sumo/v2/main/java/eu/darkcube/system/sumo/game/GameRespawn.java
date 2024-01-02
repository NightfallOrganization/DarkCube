/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.game;

import eu.darkcube.system.sumo.game.items.LifeManager;
import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;

public class GameRespawn {

    private static final Random random = new Random();
    private static GameScoreboard gameScoreboard;
    private static LifeManager lifeManager;
    private static TeamManager teamManager;

    public GameRespawn(GameScoreboard gs, LifeManager lm, TeamManager tm) {
        gameScoreboard = gs;
        lifeManager = lm;
        teamManager = tm;
    }

    public static void teleportRandomlyAroundSpawn(Player player) {
        World world = player.getWorld();

        double distance = random.nextDouble() * 6;

        double angle = random.nextDouble() * 2 * Math.PI;

        int x = (int) (distance * Math.cos(angle));
        int z = (int) (distance * Math.sin(angle));

        Location location = new Location(world, x, 110, z, player.getLocation().getYaw(), 50);
        player.teleport(location);
        gameScoreboard.updateTeamLives(teamManager, lifeManager);
    }
}
