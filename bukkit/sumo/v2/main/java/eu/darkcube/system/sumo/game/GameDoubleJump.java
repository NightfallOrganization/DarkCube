/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.game;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameDoubleJump implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, Boolean> doubleJumpCountdown = new HashMap<>();
    private final Map<UUID, Boolean> isFlying = new HashMap<>();
    private final Map<UUID, Boolean> hasDoubleJumped = new HashMap<>();

    public GameDoubleJump(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equals("world")) {
            player.setAllowFlight(false);
            player.setFlying(false);
            return;
        }
        checkJumpCountdown(player);
    }

    private void checkJumpCountdown(Player player) {
        if (isDoubleJumpCountdownActive(player)) {
            player.setAllowFlight(false);
            player.setFlying(false);
        } else {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onPlayerLand(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.isOnGround()) {
            hasDoubleJumped.put(player.getUniqueId(), false);
        }
    }

    public boolean isDoubleJumpCountdownActive(Player player) {
        return doubleJumpCountdown.getOrDefault(player.getUniqueId(), false) || isFlying.getOrDefault(player.getUniqueId(), false);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (hasDoubleJumped.getOrDefault(player.getUniqueId(), false)) {
            e.setCancelled(true);
            return;
        }

        hasDoubleJumped.put(player.getUniqueId(), true);
        if (isFlying.getOrDefault(player.getUniqueId(), false)) {
            e.setCancelled(true);
            return;
        }

        // Check if player is in SURVIVAL mode.
        if (player.getGameMode() == GameMode.SURVIVAL) {
            e.setCancelled(true);
            checkJumpCountdown(player);

            // Reset flight ability for the player (so they can't continuously double-jump).
            player.setAllowFlight(false);
            player.setFlying(false);

            // Create the double jump velocity.
            Vector jump = player.getLocation().getDirection().setY(0).normalize().multiply(0.11).setY(1.05);
            player.setVelocity(jump);

            // Set initial food level to 3.5 hunger bars.
            player.setFoodLevel(7);

            // Gradually increase food level over 63 ticks to 10 hunger bars.
            new BukkitRunnable() {
                int ticksElapsed = 0;
                final double foodIncrement = (double) (20 - 7) / 63; // The amount to increase the food level by each tick.

                @Override
                public void run() {
                    if (ticksElapsed < 63) {
                        ticksElapsed++;
                        if (ticksElapsed == 63) {
                            player.setFoodLevel(20); // Set to 10 hunger bars at the end.
                        } else {
                            player.setFoodLevel((int) (7 + ticksElapsed * foodIncrement));
                        }
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);

            // Set countdown active status for the player.
            doubleJumpCountdown.put(player.getUniqueId(), true);

            // Re-enable flight after 3 seconds (60 ticks).
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Wenn der Spieler im Spectator-Modus ist, mache nichts.
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        return;
                    }

                    doubleJumpCountdown.put(player.getUniqueId(), false);  // Set countdown inactive status for the player.
                    player.setAllowFlight(true);
                    checkJumpCountdown(player);
                }
            }.runTaskLater(plugin, 63);

        }
    }

}
