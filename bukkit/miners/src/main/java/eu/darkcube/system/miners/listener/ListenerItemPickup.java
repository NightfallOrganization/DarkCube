/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.listener;

import eu.darkcube.system.miners.items.Item;
import eu.darkcube.system.miners.items.Item.ItemKey;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ListenerItemPickup implements Listener {

    @EventHandler public void onItemPickup(PlayerPickupItemEvent e) {
        var ib = ItemBuilder.item(e.getItem().getItemStack());
        if (!ib.persistentDataStorage().has(ItemKey.ITEM)) return;
        e
                .getItem()
                .setItemStack(Item
                        .valueOf(ib.persistentDataStorage().get(ItemKey.ITEM, PersistentDataTypes.STRING))
                        .getItem(e.getPlayer(), e.getItem().getItemStack().getAmount()));
    }

}
