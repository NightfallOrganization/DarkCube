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
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.server.ServerInformation;
import eu.darkcube.system.userapi.UserAPI;
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
        UUID uuid = ItemBuilder.item(item).persistentDataStorage().get(MinigameInventory.minigameServer, PersistentDataTypes.UUID);
        if (uuid == null) return;

        p.closeInventory();
        ServerInformation information = lobby.serverManager().byUniqueId(uuid);
        if (information != null) information.connectPlayer(p.getUniqueId());
        else UserAPI.instance().user(p.getUniqueId()).sendMessage(Message.SERVER_NOT_FOUND.getMessage(p));
    }
}
