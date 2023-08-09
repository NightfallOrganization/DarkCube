/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityHealListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Überprüfe, ob das beschädigte Objekt eine lebende Entität ist und nicht ein Spieler
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            LivingEntity entity = (LivingEntity) event.getEntity();

            double damage = event.getFinalDamage();

            // Überprüfe, ob die Entität nach dem Schaden "sterben" würde
            if (entity.getHealth() - damage <= 0) {
                return;  // Wenn ja, verarbeite den Schaden normal und heile die Entität nicht
            }

            // Die aktuelle Gesundheit der Entität abrufen und den Schaden hinzufügen, um sie zu heilen
            double newHealth = Math.min(entity.getHealth() + damage, entity.getMaxHealth());
            entity.setHealth(newHealth);
        }
    }
}
