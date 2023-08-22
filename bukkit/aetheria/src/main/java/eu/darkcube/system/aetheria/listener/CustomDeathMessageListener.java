/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.aetheria.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;
import java.util.Set;

public class CustomDeathMessageListener implements Listener {
    private Set<Player> recentDeaths = new HashSet<>();

    @EventHandler public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Setzen Sie die standardmäßige Todesnachricht auf null
        event.setDeathMessage(null);

        if (!recentDeaths.contains(player)) {
            recentDeaths.add(player);

            // Senden Sie die benutzerdefinierte Todesnachricht an den Spieler
            player.sendMessage(" ");
            player.sendMessage("§7-----[§cDu bist gestorben§7]-----");
            player.sendMessage(" ");
            player.sendMessage("§7Dir wurden §c75% §7von deinen §aXP §7entfernt");
            player.sendMessage("§7Du hast §c50% §7deiner §6Coins §7verloren");
            player.sendMessage(" ");
            player.sendMessage("§7--------------------------");
            player.sendMessage(" ");
        }
    }

    @EventHandler public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        recentDeaths.remove(player);
    }
}
