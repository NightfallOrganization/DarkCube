/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.manager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BanManager implements Listener {
    private static final String TARGET_PLAYER_NAME = "Isana_Yashiro";
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getName().equalsIgnoreCase(TARGET_PLAYER_NAME)) {
            String playerIP = event.getPlayer().getAddress().getAddress().getHostAddress();
            Bukkit.getBanList(org.bukkit.BanList.Type.IP).addBan(playerIP, "Du wurdest gebannt. Grund dafür ist Frauenfeindlichkeit", null, null);
            event.getPlayer().kickPlayer("Du wurdest gebannt. Grund dafür ist Frauenfeindlichkeit");
        }
    }
}
