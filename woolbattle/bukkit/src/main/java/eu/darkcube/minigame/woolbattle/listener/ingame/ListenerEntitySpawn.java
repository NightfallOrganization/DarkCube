/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.listener.Listener;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

public class ListenerEntitySpawn extends Listener<EntitySpawnEvent> {

    @Override
    @EventHandler
    public void handle(EntitySpawnEvent e) {
        if (e.getEntityType() == EntityType.CHICKEN) {
            e.setCancelled(true);
        }
    }
}
