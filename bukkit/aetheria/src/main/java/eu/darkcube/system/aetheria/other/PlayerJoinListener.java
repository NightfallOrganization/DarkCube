/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.other;

import eu.darkcube.system.aetheria.manager.player.PlayerRegenerationManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerJoinListener implements Listener {
    private PlayerRegenerationManager playerRegenerationManager;

    public PlayerJoinListener(PlayerRegenerationManager playerRegenerationManager) {
        this.playerRegenerationManager = playerRegenerationManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerRegenerationManager.stopRegeneration(player);
        playerRegenerationManager.startRegeneration(player);
    }
}
