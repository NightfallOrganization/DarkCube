/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;

public class ListenerVoidWorld extends Listener<WorldInitEvent> {

    @Override
    @EventHandler
    public void handle(WorldInitEvent e) {
        VoidWorldPlugin.instance().loadWorld(e.getWorld());
    }
}
