/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.api.event.item.UserClickItemEvent;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.event.MinestomInventoryClickEvent;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.inventory.PlayerInventoryUtils;

public class MinestomInventoryListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(InventoryClickEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            woolbattle.api().eventManager().call(new MinestomInventoryClickEvent(user, event));

            var info = event.getClickInfo();
            var slot = switch (info) {
                case Click.Info.Left left -> left.slot();
                case Click.Info.Right right -> right.slot();
                default -> -1;
            };
            if (slot == -1) return;

            var itemStack = itemStack(slot, event.getInventory(), event.getPlayerInventory());
            if (itemStack.isAir()) return;
            var item = ItemBuilder.item(itemStack);
            woolbattle.api().eventManager().call(new UserClickItemEvent(user, item));
        });
    }

    private static ItemStack itemStack(int slot, Inventory clickedInventory, Inventory playerInventory) {
        if (slot >= clickedInventory.getSize()) {
            var converted = PlayerInventoryUtils.protocolToMinestom(slot, clickedInventory.getSize());
            return playerInventory.getItemStack(converted);
        } else {
            return clickedInventory.getItemStack(slot);
        }
    }
}
