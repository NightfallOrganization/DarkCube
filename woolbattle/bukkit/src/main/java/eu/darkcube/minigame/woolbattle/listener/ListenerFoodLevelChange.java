/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class ListenerFoodLevelChange extends Listener<FoodLevelChangeEvent> {
    @Override
    @EventHandler
    public void handle(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}
