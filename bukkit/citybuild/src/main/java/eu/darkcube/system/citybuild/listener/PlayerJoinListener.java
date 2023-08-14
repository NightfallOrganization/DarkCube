/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import eu.darkcube.system.citybuild.Citybuild;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final Citybuild citybuild;

    public PlayerJoinListener(Citybuild citybuild) {
        this.citybuild = citybuild;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        citybuild.resourcePackUtil().sendResourcePack(player);
        citybuild.setScoreboardForPlayer(player);
    }

    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
    }
}
