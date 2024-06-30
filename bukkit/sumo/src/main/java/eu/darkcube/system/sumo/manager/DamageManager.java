/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.manager;

import eu.darkcube.system.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.UUID;

public class DamageManager implements Listener {

    private final TeamManager teamManager;
    private Plugin plugin;

    public DamageManager(TeamManager teamManager, Plugin plugin) {
        this.teamManager = teamManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            UUID attackerID = attacker.getUniqueId();
            UUID victimID = victim.getUniqueId();

            ChatColor attackerTeam = teamManager.getPlayerTeam(attackerID);
            ChatColor victimTeam = teamManager.getPlayerTeam(victimID);

            // Verhindert Schaden, wenn beide Spieler im selben Team sind
            if (attackerTeam != null && attackerTeam.equals(victimTeam)) {
                event.setCancelled(true);
            }
            // Heilt das Opfer, wenn die Spieler in unterschiedlichen Teams sind
            else if (attackerTeam != null && victimTeam != null && !attackerTeam.equals(victimTeam)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (victim.isOnline()) {
                        victim.setHealth(victim.getMaxHealth());
                    }
                }, 1L); // 1 Tick später
            }
        }
    }
}
