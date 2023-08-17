/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.MinigameInventory;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ListenerMinigameServer extends BaseListener {
    private final Lobby lobby;

    public ListenerMinigameServer(Lobby lobby) {
        this.lobby = lobby;
    }

    @EventHandler public void handle(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        String itemid = ItemBuilder.item(item).persistentDataStorage().get(MinigameInventory.minigameServer, PersistentDataTypes.STRING);
        if (itemid == null || itemid.isEmpty()) {
            return;
        }
        p.closeInventory();
        lobby
                .playerManager()
                .playerExecutor(p.getUniqueId())
                .connect(lobby.cloudServiceProvider().service(UUID.fromString(itemid)).name()); // TODO async and nullable
    }
}
