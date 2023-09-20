/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MapManager implements Listener {

    private final String WORLD_NAME = "WBT-1";

    public MapManager() {
    }

    public void loadWorlds(){
        loadWorld(WORLD_NAME);
    }

    private void loadWorld(String name) {
        if(Bukkit.getWorld(name)==null){
            new WorldCreator(name).createWorld();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();



        // Optional: Teleportiere den Spieler in die Welt, wenn er beitritt
        //player.teleport(world.getSpawnLocation());
    }
}
