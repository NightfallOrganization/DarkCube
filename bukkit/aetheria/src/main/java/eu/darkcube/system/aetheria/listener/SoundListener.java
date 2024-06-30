/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class SoundListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Überprüfen, ob der Verursacher des Schadens ein Spieler ist
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            // Wenn das Entity nicht ein Spieler ist
            if(!(event.getEntity() instanceof Player)) {
                // Sound beim Schlagen abspielen
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 0.8f);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Überprüfen, ob der Verursacher des Schadens ein Spieler war
        if(event.getEntity().getKiller() instanceof Player) {
            Player player = event.getEntity().getKiller();

            // Wenn das Entity nicht ein Spieler war
            if(!(event.getEntity() instanceof Player)) {
                // Sound beim Töten abspielen
//                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
            }
        }
    }
}
