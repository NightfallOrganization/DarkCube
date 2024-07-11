/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.listener.command;

import eu.darkcube.system.bukkit.Plugin;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class ChainCommandListener implements Listener {
    public static Key chainKey;

    public ChainCommandListener(Plugin plugin) {
        chainKey = Key.key(plugin, "chained");
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());
        int chainValue = user.metadata().get(chainKey);

        if (chainValue == 1) {
            event.setCancelled(true);
            user.sendMessage(Message.CHAIN_CANT_DO_COMMAND);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());
        int chainValue = user.metadata().get(chainKey);
        if (chainValue == 1) {
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());
        int chainValue = user.metadata().get(chainKey);
        if (chainValue == 1) {
            event.setCancelled(true);
        }
    }

}
