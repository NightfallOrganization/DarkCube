/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.listener;

import java.util.UUID;

import eu.darkcube.system.pserver.plugin.inventory.UserManagmentInventory;
import eu.darkcube.system.pserver.plugin.inventory.UserManagmentUserInventory;
import eu.darkcube.system.pserver.plugin.user.User;
import eu.darkcube.system.pserver.plugin.user.UserManager;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class UserManagmentInventoryListener implements BaseListener {

    private final UserManagmentInventory inventory;

    public UserManagmentInventoryListener(UserManagmentInventory inventory) {
        this.inventory = inventory;
    }

    @EventHandler public void handle(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        User user = UserManager.getInstance().getUser(player.getUniqueId());
        if (!inventory.isOpened(player)) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemBuilder builder = ItemBuilder.item(item);
        if (!builder.persistentDataStorage().has(UserManagmentInventory.KEY)) {
            return;
        }
        if (!builder
                .persistentDataStorage()
                .get(UserManagmentInventory.KEY, PersistentDataTypes.STRING)
                .equals(UserManagmentInventory.KEY_VALUE)) {
            return;
        }
        UUID uuid = UUID.fromString(builder.persistentDataStorage().get(UserManagmentInventory.USER_UUID_KEY, PersistentDataTypes.STRING));
        String name = builder.persistentDataStorage().get(UserManagmentInventory.USER_NAME_KEY, PersistentDataTypes.STRING);
        new UserManagmentUserInventory(user, uuid, name).open();
    }
}
