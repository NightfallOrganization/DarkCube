/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.api.event.user.UserMoveEvent;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;

public class MinestomMoveListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(PlayerMoveEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var pos = event.getNewPosition();
            var user = player.user();
            if (user != null) {
                var instance = event.getInstance();
                var world = woolbattle.worlds().get(instance);
                var newLocation = new Location(world, pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch());
                var moveEvent = new UserMoveEvent(user, newLocation);
                woolbattle.api().eventManager().call(moveEvent);
                if (moveEvent.cancelled()) {
                    event.setCancelled(true);
                    return;
                }
                user.location(newLocation);
            }
        });
    }
}
