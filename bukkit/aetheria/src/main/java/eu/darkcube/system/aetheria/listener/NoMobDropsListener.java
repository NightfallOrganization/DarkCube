/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.aetheria.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class NoMobDropsListener implements Listener {
    @EventHandler public void onMobDeath(EntityDeathEvent event) {
        // Leeren der Drop-Liste
        event.getDrops().clear();
        // Erfahrungspunkte auf 0 setzen
        event.setDroppedExp(0);
    }
}
