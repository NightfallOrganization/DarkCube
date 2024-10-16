/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.executions;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.manager.LifeManager;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.manager.MapManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Respawn implements Listener {
    private MapManager mainRuler;
    private Random random;
    private LifeManager lifeManager;
    private TeamManager teamManager;
    private final Map<UUID, Long> playerHeightTime = new HashMap<>();

    public Respawn(MapManager mainRuler, LifeManager lifeManager, TeamManager teamManager) {
        this.mainRuler = mainRuler;
        this.random = new Random();
        this.lifeManager = lifeManager;
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (player.getWorld().equals(mainRuler.getActiveWorld()) && event.getTo().getY() < 80) {
            teleportPlayerRandomly(player);
            lifeManager.reduceLife(player.getUniqueId());
        }

        if (player.getLocation().getY() > 115 && player.getGameMode() != GameMode.SPECTATOR) {
            if (!playerHeightTime.containsKey(playerUUID)) {
                playerHeightTime.put(playerUUID, System.currentTimeMillis());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.getLocation().getY() > 115) {
                            if (System.currentTimeMillis() - playerHeightTime.get(playerUUID) >= 10000) {
                                player.sendMessage("§cDu warst zu lange, zu hoch!");
                                teleportPlayerRandomly(player);
                                playerHeightTime.remove(playerUUID);
                                this.cancel();
                            }
                        } else {
                            playerHeightTime.remove(playerUUID);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Sumo.getInstance(), 0, 20);
            }
        } else {
            playerHeightTime.remove(playerUUID);
        }
    }

    public void teleportPlayerRandomly(Player player) {
        World world = mainRuler.getActiveWorld();
        int x = random.nextInt(13) - 6; // Zufällige X-Koordinate im Bereich von -6 bis +6
        int z = random.nextInt(13) - 6; // Zufällige Z-Koordinate im Bereich von -6 bis +6
        Location respawnLocation = new Location(world, x, 107, z);
        respawnLocation.setYaw(getLookAtYaw(respawnLocation, new Location(world, 0.5, 107, 0.5)));
        respawnLocation.setPitch(25);
        player.teleport(respawnLocation);
    }

    private float getLookAtYaw(Location from, Location to) {
        double deltaX = to.getX() - from.getX();
        double deltaZ = to.getZ() - from.getZ();
        double yaw = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;
        if (yaw < 0) {
            yaw += 360;
        }
        return (float) yaw;
    }

}
