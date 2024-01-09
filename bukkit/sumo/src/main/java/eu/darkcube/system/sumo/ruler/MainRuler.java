/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.ruler;

import eu.darkcube.system.sumo.scoreboards.LobbyScoreboard;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.List;
import java.util.Arrays;

public class MainRuler implements Listener {
    private World activeWorld;
    private JavaPlugin plugin;
    private LobbyScoreboard lobbyScoreboard;
    private static final List<String> AVAILABLE_MAPS = Arrays.asList("Origin", "Atlas", "Demonic");

    public MainRuler(JavaPlugin plugin, LobbyScoreboard lobbyScoreboard) {
        this.plugin = plugin;
        this.lobbyScoreboard = lobbyScoreboard;
        String worldName = AVAILABLE_MAPS.get(new Random().nextInt(AVAILABLE_MAPS.size()));
        setActiveWorld(Bukkit.getWorld(worldName));
    }

    public void setActiveWorld(World world) {
        this.activeWorld = world;
        this.lobbyScoreboard.updateMap(world.getName());
        this.activeWorld.setGameRuleValue("keepInventory", "true");
    }

    public World getActiveWorld() {
        return this.activeWorld;
    }

}
