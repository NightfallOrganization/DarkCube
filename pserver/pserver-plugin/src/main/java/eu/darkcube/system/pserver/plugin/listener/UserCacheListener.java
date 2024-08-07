/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.listener;

import eu.darkcube.system.pserver.plugin.user.UserCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserCacheListener extends SingleInstanceBaseListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PlayerJoinEvent event) {
        UserCache.cache().update(event.getPlayer());
    }

}
