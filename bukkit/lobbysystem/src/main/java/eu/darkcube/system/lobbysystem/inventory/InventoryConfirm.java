/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory;

import eu.darkcube.system.bukkit.inventoryapi.v1.AsyncPagedInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryConfirm extends LobbyAsyncPagedInventory {

    private static final InventoryType type_confirm = InventoryType.of("confirm");

    public final Runnable onConfirm;
    public final Runnable onCancel;

    public InventoryConfirm(Component title, User user, Runnable onConfirm, Runnable onCancel) {
        super(InventoryConfirm.type_confirm, title, 3 * 9, AsyncPagedInventory.box(1, 1, 3, 9), user);
        // super(title, null, 3 * 9, InventoryConfirm.type_confirm, box(1, 1, 3, 9));
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    @Override protected void fillItems(Map<Integer, ItemStack> items) {
        super.fillItems(items);
        items.put(IInventory.slot(2, 3), Item.CANCEL.getItem(this.user.user()));
        items.put(IInventory.slot(2, 7), Item.CONFIRM.getItem(this.user.user()));
    }

    @Override protected void insertFallbackItems() {
        ItemStack l = Item.LIGHT_GRAY_GLASS_PANE.getItem(this.user.user());
        for (int slot : this.slots) {
            this.fallbackItems.put(slot, l);
        }
        super.insertFallbackItems();
    }

}
