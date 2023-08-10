/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import eu.darkcube.system.citybuild.util.CustomHealthManager;

public class DamageListener implements Listener {

    private CustomHealthManager healthManager;

    public DamageListener(CustomHealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            int damage = (int) (event.getDamage() * 1);  // da 1 Herz = 2 Health
            int currentHealth = healthManager.getHealth(player);

            if (currentHealth <= damage) {
                player.setHealth(0.0);  // Setze den Spieler Gesundheitswert auf 0, was zum Tod führt
            } else {
                healthManager.setHealth(player, currentHealth - damage);
                event.setDamage(0.0);  // Setze den normalen Schaden auf 0
            }
        }
    }
}
