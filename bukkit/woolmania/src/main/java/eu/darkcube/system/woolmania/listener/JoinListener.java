/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        WoolMania.getInstance().woolManiaPlayerMap.put(player, new WoolManiaPlayer(player));
        // WoolManiaPlayer woolManiaPlayer = WoolMania.woolManiaPlayerMap.get(player);
    }

}