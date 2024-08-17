/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.api.event.user.UserChatEvent;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;

public class MinestomChatListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(PlayerChatEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return; // TODO setup mode?
            woolbattle.api().eventManager().call(new UserChatEvent(user, event.getMessage()));
            event.setCancelled(true);
        });
    }
}
