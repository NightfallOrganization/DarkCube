/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.manager.WorldManager.SPAWN;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.TeleportLocations;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Block block = event.getTo().getBlock();
        Player player = event.getPlayer();

        if (player.getWorld().equals(SPAWN) && player.getLocation().getY() < 110 && player.getGameMode() != GameMode.CREATIVE) {
            player.teleport(TeleportLocations.SPAWN.getLocation());
        }

        if (block.getType() == Material.NETHER_PORTAL) {
            Vector knockback = event.getPlayer().getLocation().getDirection().multiply(-1).setY(0.5);
            event.getPlayer().setVelocity(knockback);

            Bukkit.getScheduler().runTaskLater(WoolMania.getInstance(), () -> {
                WoolMania.getInstance().getTeleportInventory().openInventory(player);
            }, 5L);
        }

    }

}
