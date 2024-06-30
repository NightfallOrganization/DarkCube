/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.wool;

import eu.darkcube.system.woolbattleteamfight.Main;
import eu.darkcube.system.woolbattleteamfight.team.TeamManager;
import eu.darkcube.system.woolbattleteamfight.items.other.DoubleJump;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.bukkit.*;


import java.util.HashMap;
import java.util.Map;

public class WoolManager implements Listener {

    private final TeamManager teamManager;
    private final Map<Block, Player> playerPlacedBlocks = new HashMap<>();

    public WoolManager(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);

        if (player.getWorld().getName().equalsIgnoreCase("world")) {
            return;
        }

        event.setCancelled(true);

        if (block.getType() == Material.WOOL) {
            final DyeColor currentColor = DyeColor.getByWoolData(block.getData());

            if (playerPlacedBlocks.containsKey(block)) {
                block.setType(Material.AIR);
                playerPlacedBlocks.remove(block);
            } else {
                block.setType(Material.AIR); // Setze den Block zuerst auf Luft

                // Erstelle eine verzögerte Aufgabe, um den Block zurückzusetzen
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), new Runnable() {
                    @Override
                    public void run() {
                        block.setType(Material.WOOL); // Setze den Blocktyp wieder auf Wolle
                        block.setData(currentColor.getWoolData());
                    }
                }, 5L); // 30 Ticks = 1,5 Sekunden
            }

            if (!hasMoreWool(player)) {
                ItemStack wool = getWoolForTeam(teamManager.playerTeams.get(player));
                wool.setAmount(2);
                player.getInventory().addItem(wool);
            }

            updateFlyAbility(player);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() == Material.WOOL && !player.getWorld().getName().equalsIgnoreCase("world")) {
            playerPlacedBlocks.put(block, player);
        }
    }

    public void addPlayerPlacedBlock(Block block, Player player) {
        playerPlacedBlocks.put(block, player);
    }

    public boolean isPlayerPlacedBlock(Block block) {
        return playerPlacedBlocks.containsKey(block);
    }

    public void removePlayerPlacedBlock(Block block) {
        playerPlacedBlocks.remove(block);
    }

    private void updateFlyAbility(Player player) {
        DoubleJump doubleJump = JavaPlugin.getPlugin(Main.class).getDoubleJump();

        player.setAllowFlight(false);
        player.setFlying(false);

        if (doubleJump.isDoubleJumpCountdownActive(player)) {
            return;
        }

        if (WoolReducer.hasEnoughWool(player, 5)) {
            player.setAllowFlight(true);
        }
    }



    private boolean hasMoreWool(Player player) {
        int woolItemCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.WOOL) {
                woolItemCount += item.getAmount();
            }
        }
        return woolItemCount >= 192;
    }

    private ItemStack getWoolForTeam(Team team) {
        DyeColor color;
        if (team == null) {
            color = DyeColor.WHITE;
        } else {
            switch (team.getName()) {
                case "Red":
                    color = DyeColor.RED;
                    break;
                case "Blue":
                    color = DyeColor.BLUE;
                    break;
                case "Violet":
                    color = DyeColor.PURPLE;
                    break;
                case "Green":
                    color = DyeColor.GREEN;
                    break;
                default:
                    color = DyeColor.WHITE;
                    break;
            }
        }
        return new ItemStack(Material.WOOL, 1, color.getWoolData());
    }

}
