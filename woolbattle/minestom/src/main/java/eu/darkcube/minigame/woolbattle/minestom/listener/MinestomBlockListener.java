/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomInstance;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public class MinestomBlockListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(PlayerBlockBreakEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            var instance = event.getInstance();
            var world = ((MinestomInstance) instance).world();
            var pos = event.getBlockPosition();
            var block = world.blockAt(pos.blockX(), pos.blockY(), pos.blockZ());
            var breakResult = woolbattle.eventHandler().blockBreak(user, block);
            if (breakResult.cancel()) {
                event.setCancelled(true);
            }
        });
        node.addListener(PlayerBlockPlaceEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            var instance = event.getInstance();
            var world = ((MinestomInstance) instance).world();
            var pos = event.getBlockPosition();
            var block = world.blockAt(pos.blockX(), pos.blockY(), pos.blockZ());
            var item = ItemBuilder.item(event.getPlayer().getInventory().getItemInHand(event.getHand()));
            var placeResult = woolbattle.eventHandler().blockPlace(user, block, item);
            if (placeResult.cancel()) {
                event.setCancelled(true);
            }
        });
    }
}
