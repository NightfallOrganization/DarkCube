/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.listener;

import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryLobbySwitcher;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AsyncExecutor;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerLobbySwitcher extends BaseListener {
    private final Lobby lobby;

    public ListenerLobbySwitcher(Lobby lobby) {
        this.lobby = lobby;
    }

    @EventHandler public void handle(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        Player p = (Player) e.getWhoClicked();
        User user = UserAPI.getInstance().getUser(p);
        String serverId = ItemBuilder.item(item).persistentDataStorage().get(InventoryLobbySwitcher.server, PersistentDataTypes.STRING);
        if (serverId == null || serverId.isEmpty()) return;

        ServiceInfoSnapshot service = lobby.cloudServiceProvider().serviceByName(serverId); // TODO Async
        if (service == null) {
            UserWrapper.fromUser(user).setOpenInventory(new InventoryLobbySwitcher(user));
            return;
        }
        if (!service.connected()) {
            user.sendMessage(Message.SERVER_NOT_STARTED);
            p.closeInventory();
            return;
        }
        p.closeInventory();
        UserWrapper.fromUser(user).setLastPosition(p.getLocation());
        AsyncExecutor.service().submit(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            lobby.playerManager().playerExecutor(p.getUniqueId()).connect(serverId);
        });
    }
}
