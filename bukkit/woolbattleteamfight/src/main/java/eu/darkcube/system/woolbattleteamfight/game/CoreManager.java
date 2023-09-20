/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.game;

import eu.darkcube.system.woolbattleteamfight.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.*;

import org.bukkit.scheduler.BukkitRunnable;

public class CoreManager implements Listener {

    private final Map<String, Location> coreLocations = new HashMap<>();
    private final Map<String, Integer> coreCharges = new HashMap<>();
    private final Map<UUID, BukkitRunnable> sneakingTasks = new HashMap<>();
    public static final Set<UUID> markedForRemoval = new HashSet<>();
    private TeamManager teamManager;

    public CoreManager(TeamManager teamManager) {
        this.teamManager = teamManager;

        World world = Bukkit.getWorld("WBT-1");
        coreLocations.put("Red", new Location(world, 0, 120, -46));
        coreLocations.put("Blue", new Location(world, -46, 120, 0));
        coreLocations.put("Green", new Location(world, 0, 120, 46));
        coreLocations.put("Violet", new Location(world, 46, 120, 0));

        coreCharges.put("Red", 100);
        coreCharges.put("Blue", 100);
        coreCharges.put("Green", 100);
        coreCharges.put("Violet", 100);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking()) {
            coreLocations.forEach((team, loc) -> {
                if (player.getLocation().getWorld().equals(loc.getWorld())
                        && player.getLocation().getBlockX() == loc.getBlockX()
                        && player.getLocation().getBlockZ() == loc.getBlockZ()
                        && player.getLocation().getBlockY() == loc.getBlockY() + 1) {

                    if (teamManager.isPlayerInTeam(player) && teamManager.getPlayerTeam(player).equals(team)) {
                        return;
                    }

                    if (!sneakingTasks.containsKey(player.getUniqueId())) {
                        BukkitRunnable task = new BukkitRunnable() {
                            @Override
                            public void run() {

                                int currentCharge = coreCharges.get(team);
                                currentCharge -= 5;
                                if (currentCharge < 0) {
                                    currentCharge = 0;
                                    this.cancel();
                                }

                                if (currentCharge == 0) {
                                    for (Player teamPlayer : Bukkit.getOnlinePlayers()) {
                                        if (teamManager.isPlayerInTeam(teamPlayer) && teamManager.getPlayerTeam(teamPlayer).equals(team)) {
                                            markedForRemoval.add(teamPlayer.getUniqueId());
                                        }
                                    }
                                }

                                coreCharges.put(team, currentCharge);
                                Bukkit.broadcastMessage(getCoreMessage(team, currentCharge));
                            }
                        };

                        task.runTaskTimer(Bukkit.getPluginManager().getPlugin("WoolBattleTeamfight"), 20L, 20L);
                        sneakingTasks.put(player.getUniqueId(), task);
                    }
                }
            });
        } else {
            BukkitRunnable task = sneakingTasks.remove(player.getUniqueId());
            if (task != null) {
                task.cancel();
            }
        }
    }

    private String getCoreMessage(String team, int charge) {
        String colorCode = "";
        switch (team) {
            case "Red":
                colorCode = "§c";
                break;
            case "Blue":
                colorCode = "§9";
                break;
            case "Green":
                colorCode = "§a";
                break;
            case "Violet":
                colorCode = "§5";
                break;
        }
        return "§7" + team + " Core: " + colorCode + charge + "%";
    }
}
