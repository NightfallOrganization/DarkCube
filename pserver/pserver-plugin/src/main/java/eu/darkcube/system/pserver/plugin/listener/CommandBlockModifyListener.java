/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.listener;

import eu.darkcube.system.pserver.plugin.command.impl.PServer;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandBlockModifyListener extends SingleInstanceBaseListener {

    @EventHandler
    public void handle(ServerCommandEvent event) {
        var sender = event.getSender();
        if (sender instanceof BlockCommandSender bsender) {
            var command = event.getCommand();
            var commandName = command.split(" ")[0];
            if (!PServer.TOTAL_COMMAND_NAMES.contains(commandName)) {
                bsender.sendMessage("Invalid command: " + commandName);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        var action = event.getAction();
        var clicked = event.getClickedBlock();
        var item = event.getItem();
        var face = event.getBlockFace();
        var player = event.getPlayer();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (item != null && item.getType() == Material.COMMAND) {
                var toPlace = clicked.getRelative(face);
                if (clicked.getType() != Material.COMMAND || player.isSneaking()) {
                    event.setUseInteractedBlock(Result.DENY);
                    switch (player.getGameMode()) {
                        case SURVIVAL:
                            item.setAmount(item.getAmount() - 1);
                            event.getPlayer().setItemInHand(item);
                            toPlace.setType(Material.COMMAND);
                            break;
                        case CREATIVE:
                            toPlace.setType(Material.COMMAND);
                            break;
                        case ADVENTURE, SPECTATOR:
                            event.setCancelled(true);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + player.getGameMode());
                    }
                }
            } else {
                if (event.getClickedBlock().getType() == Material.COMMAND) {
                    switch (player.getGameMode()) {
                        case CREATIVE:
                        case SURVIVAL:
                            event.setCancelled(true);
                            player.closeInventory();
                            break;
                        default:
                            event.setCancelled(true);
                            break;
                    }
                }
            }
        }
    }
}
