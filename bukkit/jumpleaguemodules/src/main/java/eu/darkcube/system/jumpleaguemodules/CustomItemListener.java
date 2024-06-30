/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.jumpleaguemodules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static eu.darkcube.system.libs.com.google.gson.internal.bind.TypeAdapters.UUID;

public class CustomItemListener implements Listener {
    private Main plugin;
    private HashMap<UUID, String> lastSteppedOnPlate = new HashMap<>();

    public CustomItemListener(Main plugin) {
        this.plugin = plugin;
        PlayerDataHandler.setup(plugin.getDataFolder());
        this.lastSteppedOnPlate = PlayerDataHandler.loadPlayers();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.GOLD_PLATE) {
            Player player = event.getPlayer();
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            player.getInventory().addItem(CustomItem.getRespawnItem());
        }
    }

    private boolean hasResetItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.INK_SACK && item.getDurability() == 1) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && "§cReset".equals(meta.getDisplayName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.GOLD_PLATE) {
            Player player = event.getPlayer();
            String currentPlate = event.getClickedBlock().getLocation().toString();

            String lastPlate = lastSteppedOnPlate.get(player.getUniqueId());

            // Wenn der Spieler das Reset-Item nicht hat, füge es seinem Inventar hinzu
            if (!hasResetItem(player)) {
                ItemStack resetDye = new ItemStack(Material.INK_SACK, 1, (short) 1);
                ItemMeta dyeMeta = resetDye.getItemMeta();
                dyeMeta.setDisplayName("§cReset");
                resetDye.setItemMeta(dyeMeta);
                player.getInventory().setItem(0, resetDye);
            }

            if (lastPlate == null || !lastPlate.equals(currentPlate)) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                lastSteppedOnPlate.put(player.getUniqueId(), currentPlate);

                PlayerDataHandler.get().createSection("playersStepped", lastSteppedOnPlate);
                PlayerDataHandler.save();
            }

            Main.respawnLocations.put(player.getUniqueId(), event.getClickedBlock().getLocation().add(0.5, 1, 0.5));
        }
    }

    @EventHandler
    public void onPlayerUseDye(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();

        // Überprüft, ob der Gegenstand in der Hand roter Farbstoff mit dem Namen "Reset" ist
        if (itemInHand != null && itemInHand.getType() == Material.INK_SACK && itemInHand.getDurability() == 1) {
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta != null && "§cReset".equals(meta.getDisplayName())) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Location lastPlateLocation = Main.respawnLocations.get(player.getUniqueId());
                    if (lastPlateLocation != null) {
                        player.teleport(lastPlateLocation);
                        // Spielen Sie den Villager Schaden Sound ab
                        player.playSound(player.getLocation(), Sound.VILLAGER_HIT, 1.0F, 0.8F);
                    }
                }
            }
        }
    }



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getTo().getY() < plugin.getConfig().getDouble("respawnHeight", 70)) {
            Location respawnLocation = Main.respawnLocations.get(player.getUniqueId());
            if (respawnLocation != null) {
                player.teleport(respawnLocation);
                player.playSound(player.getLocation(), Sound.VILLAGER_HIT, 1.0F, 0.8F);
            }
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location respawnLocation = Main.respawnLocations.get(player.getUniqueId());

        if (respawnLocation != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    player.spigot().respawn();
                    player.teleport(respawnLocation);
                }
            }, 1L);
        }
    }
}
