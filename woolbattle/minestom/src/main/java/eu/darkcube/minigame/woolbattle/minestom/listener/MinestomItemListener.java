/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.api.entity.ItemEntity;
import eu.darkcube.minigame.woolbattle.api.event.item.UserDropItemEvent;
import eu.darkcube.minigame.woolbattle.api.event.item.UserPickupItemEvent;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.EntityImpl;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;

public class MinestomItemListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(ItemDropEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            var itemStack = event.getItemStack();
            var item = ItemBuilder.item(itemStack);
            var userDropItemEvent = new UserDropItemEvent(user, item);
            woolbattle.api().eventManager().call(userDropItemEvent);
            if (userDropItemEvent.cancelled()) {
                event.setCancelled(true);
            }
        });
        node.addListener(PickupItemEvent.class, event -> {
            var player = (MinestomPlayer) event.getLivingEntity();
            var user = player.user();
            if (user == null) return;
            var itemEntity = event.getItemEntity();
            var entity = (ItemEntity) ((EntityImpl) itemEntity).handle();
            var itemStack = event.getItemStack();
            var item = ItemBuilder.item(itemStack);
            var userPickupItemEvent = new UserPickupItemEvent(user, entity, item);
            woolbattle.api().eventManager().call(userPickupItemEvent);
            if (userPickupItemEvent.cancelled()) {
                event.setCancelled(true);
            }
        });
    }
}
