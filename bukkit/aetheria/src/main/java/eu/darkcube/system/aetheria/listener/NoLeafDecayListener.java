/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.aetheria.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

public class NoLeafDecayListener implements Listener {

    @EventHandler public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }
}
