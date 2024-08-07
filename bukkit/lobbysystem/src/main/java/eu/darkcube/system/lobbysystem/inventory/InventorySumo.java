/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory;

import java.util.Set;

import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.MinigameInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;

public class InventorySumo extends MinigameInventory {

    private static final InventoryType type_sumo = InventoryType.of("sumo");

    public InventorySumo(User user) {
        super(Item.INVENTORY_COMPASS_SUMO.getDisplayName(user), Item.INVENTORY_COMPASS_SUMO, InventorySumo.type_sumo, user);
    }

    // @Override
    // protected ItemStack getMinigameItem(User user) {
    // return Item.INVENTORY_COMPASS_WOOLBATTLE.getItem(user);
    // }

    @Override
    protected Set<String> getCloudTasks() {
        return Lobby.getInstance().getDataManager().getSumoTasks();
    }

}
