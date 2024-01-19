/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import eu.darkcube.system.sumo.ruler.MainRuler;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameDoubleJump implements Listener {
    private final JavaPlugin plugin;
    private final MainRuler mainRuler;
    private final Map<UUID, Boolean> doubleJumpCountdown = new HashMap<>();
    private final Map<UUID, Boolean> isFlying = new HashMap<>();
    private final Map<UUID, Boolean> hasDoubleJumped = new HashMap<>();

    public GameDoubleJump(JavaPlugin plugin, MainRuler mainRuler) {
        this.plugin = plugin;
        this.mainRuler = mainRuler;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!player.getWorld().equals(mainRuler.getActiveWorld())) {  // Überprüfen, ob der Spieler sich in der ActiveMap befindet
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

        if (player.getGameMode() == GameMode.SPECTATOR || !player.getWorld().equals(mainRuler.getActiveWorld())) {
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

        if (player.getGameMode() == GameMode.SURVIVAL) {
            e.setCancelled(true);
            checkJumpCountdown(player);

            player.setAllowFlight(false);
            player.setFlying(false);


            Vector jump = player.getLocation().getDirection().setY(0).normalize().multiply(0.11).setY(1.05);
            player.setVelocity(jump);

            player.setFoodLevel(7);

            new BukkitRunnable() {
                int ticksElapsed = 0;
                final double foodIncrement = (double) (20 - 7) / 63;

                @Override
                public void run() {
                    if (ticksElapsed < 63) {
                        ticksElapsed++;
                        if (ticksElapsed == 63) {
                            player.setFoodLevel(20);
                        } else {
                            player.setFoodLevel((int) (7 + ticksElapsed * foodIncrement));
                        }
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);

            doubleJumpCountdown.put(player.getUniqueId(), true);

            new BukkitRunnable() {
                @Override
                public void run() {

                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        return;
                    }

                    doubleJumpCountdown.put(player.getUniqueId(), false);
                    player.setAllowFlight(true);
                    checkJumpCountdown(player);
                }
            }.runTaskLater(plugin, 63);

        }
    }

}
