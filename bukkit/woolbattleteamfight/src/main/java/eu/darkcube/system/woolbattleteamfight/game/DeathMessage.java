/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.game;

import eu.darkcube.system.woolbattleteamfight.Main;
import eu.darkcube.system.woolbattleteamfight.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scoreboard.Team;

public class DeathMessage implements Listener {

    private TeamManager teamManager;
    private HashMap<UUID, UUID> lastAttacker = new HashMap<>();  // stores victim -> attacker
    private HashMap<UUID, Integer> countdownTasks = new HashMap<>(); // stores victim -> countdown task id

    public DeathMessage(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();

        if (event.getDamager() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            Player attacker = (Player) event.getDamager();
            startCountdown(attacker, victim);
        } else if (event.getDamager() instanceof Snowball || event.getDamager() instanceof Arrow) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();
                startCountdown(shooter, victim);
            }
        }
    }

    private void startCountdown(Player attacker, Player victim) {
        lastAttacker.put(victim.getUniqueId(), attacker.getUniqueId());

        // Cancel previous countdown if one exists
        if (countdownTasks.containsKey(victim.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(countdownTasks.get(victim.getUniqueId()));
        }

        // Start a new countdown
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            lastAttacker.remove(victim.getUniqueId());
            countdownTasks.remove(victim.getUniqueId());
        }, 20L * 10);  // 10 seconds

        countdownTasks.put(victim.getUniqueId(), taskId);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() <= 90 && lastAttacker.containsKey(player.getUniqueId())) {
            Player attacker = Bukkit.getPlayer(lastAttacker.get(player.getUniqueId()));
            if (attacker != null) {
                String victimTeamColor = teamManager.isPlayerInTeam(player) ? teamManager.playerTeams.get(player).getPrefix() : "§7";
                String attackerTeamColor = teamManager.isPlayerInTeam(attacker) ? teamManager.playerTeams.get(attacker).getPrefix() : "§7";

                Bukkit.broadcastMessage("§7Der Spieler " + victimTeamColor + player.getName() + " §7wurde von " + attackerTeamColor + attacker.getName() + " §7getötet");

                lastAttacker.remove(player.getUniqueId());
                if (countdownTasks.containsKey(player.getUniqueId())) {
                    Bukkit.getScheduler().cancelTask(countdownTasks.get(player.getUniqueId()));
                    countdownTasks.remove(player.getUniqueId());
                }

                // Überprüfen Sie, ob der Spieler zur Entfernung markiert ist
                if (CoreManager.markedForRemoval.contains(player.getUniqueId())) {
                    Team playerTeam = teamManager.playerTeams.get(player);
                    if (playerTeam != null) {

                        Bukkit.broadcastMessage("§7Der Spieler " + victimTeamColor + player.getName() + " §7ist ausgeschieden");

                        player.getInventory().clear();
                        player.setGameMode(GameMode.SPECTATOR);

                        playerTeam.removeEntry(player.getName());
                        teamManager.playerTeams.remove(player);
                        CoreManager.markedForRemoval.remove(player.getUniqueId());
                    }
                }
            }
        }
    }

}
