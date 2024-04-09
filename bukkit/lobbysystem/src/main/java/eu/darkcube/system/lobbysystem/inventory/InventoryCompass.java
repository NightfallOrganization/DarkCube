/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.inventory;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryCompass extends LobbyAsyncPagedInventory {

    private static final InventoryType type_compass = InventoryType.of("compass");

    public InventoryCompass(User user) {
        // super(InventoryCompass.type_compass,
        // Bukkit.createInventory(null, 6 * 9,
        // ChatColor.stripColor(Item.INVENTORY_COMPASS.getDisplayName(user))));
        super(InventoryCompass.type_compass, Item.INVENTORY_COMPASS.getDisplayName(user), 6 * 9, box(2, 2, 4, 8), user);
    }

    @Override protected void insertFallbackItems() {
        ItemStack m = ItemBuilder
                .item(Material.STAINED_GLASS_PANE)
                .damage(2)
                .displayname(Component.text(" ").color(NamedTextColor.GOLD))
                .build();
        ItemStack p = ItemBuilder
                .item(Material.STAINED_GLASS_PANE)
                .damage(10)
                .displayname(Component.text(" ").color(NamedTextColor.GOLD))
                .build();
        ItemStack l = ItemBuilder
                .item(Material.STAINED_GLASS_PANE)
                .damage(7)
                .displayname(Component.text(" ").color(NamedTextColor.GOLD))
                .build();
        ItemStack d = ItemBuilder
                .item(Material.STAINED_GLASS_PANE)
                .damage(15)
                .displayname(Component.text(" ").color(NamedTextColor.GOLD))
                .build();
        this.fallbackItems.put(IInventory.slot(1, 1), p);
        this.fallbackItems.put(IInventory.slot(1, 2), m);
        this.fallbackItems.put(IInventory.slot(1, 3), p);
        this.fallbackItems.put(IInventory.slot(1, 4), m);
        this.fallbackItems.put(IInventory.slot(1, 5), p);
        this.fallbackItems.put(IInventory.slot(1, 6), m);
        this.fallbackItems.put(IInventory.slot(1, 7), p);
        this.fallbackItems.put(IInventory.slot(1, 8), m);
        this.fallbackItems.put(IInventory.slot(1, 9), p);
        this.fallbackItems.put(IInventory.slot(2, 1), m);
        this.fallbackItems.put(IInventory.slot(3, 1), p);
        this.fallbackItems.put(IInventory.slot(4, 1), m);
        this.fallbackItems.put(IInventory.slot(6, 1), p);
        this.fallbackItems.put(IInventory.slot(6, 2), m);
        this.fallbackItems.put(IInventory.slot(6, 3), p);
        this.fallbackItems.put(IInventory.slot(6, 4), m);
        this.fallbackItems.put(IInventory.slot(6, 5), p);
        this.fallbackItems.put(IInventory.slot(6, 6), m);
        this.fallbackItems.put(IInventory.slot(6, 7), p);
        this.fallbackItems.put(IInventory.slot(6, 8), m);
        this.fallbackItems.put(IInventory.slot(6, 9), p);
        this.fallbackItems.put(IInventory.slot(4, 9), m);
        this.fallbackItems.put(IInventory.slot(3, 9), m);
        this.fallbackItems.put(IInventory.slot(2, 9), m);

        this.fallbackItems.put(IInventory.slot(2, 2), d);
        this.fallbackItems.put(IInventory.slot(2, 3), l);
        this.fallbackItems.put(IInventory.slot(2, 4), d);
        this.fallbackItems.put(IInventory.slot(2, 5), l);
        this.fallbackItems.put(IInventory.slot(2, 6), d);
        this.fallbackItems.put(IInventory.slot(2, 7), l);
        this.fallbackItems.put(IInventory.slot(2, 8), d);
        this.fallbackItems.put(IInventory.slot(3, 2), l);
        this.fallbackItems.put(IInventory.slot(3, 3), l);
        this.fallbackItems.put(IInventory.slot(3, 4), l);
        this.fallbackItems.put(IInventory.slot(3, 5), l);
        this.fallbackItems.put(IInventory.slot(3, 6), l);
        this.fallbackItems.put(IInventory.slot(3, 7), l);
        this.fallbackItems.put(IInventory.slot(3, 8), l);
        this.fallbackItems.put(IInventory.slot(4, 2), d);
        this.fallbackItems.put(IInventory.slot(4, 3), l);
        this.fallbackItems.put(IInventory.slot(4, 4), d);
        this.fallbackItems.put(IInventory.slot(4, 5), l);
        this.fallbackItems.put(IInventory.slot(4, 6), d);
        this.fallbackItems.put(IInventory.slot(4, 7), l);
        this.fallbackItems.put(IInventory.slot(4, 8), d);

        this.fallbackItems.put(IInventory.slot(3, 5), Item.INVENTORY_COMPASS_SPAWN.getItem(this.user.user()));
        this.fallbackItems.put(IInventory.slot(3, 3), Item.INVENTORY_COMPASS_WOOLBATTLE.getItem(this.user.user()));
        this.fallbackItems.put(IInventory.slot(3, 7), Item.INVENTORY_COMPASS_SUMO.getItem(user.user()));
        this.fallbackItems.put(IInventory.slot(4, 6), Item.INVENTORY_COMPASS_JUMPANDRUN.getItem(user.user()));
        this.fallbackItems.put(IInventory.slot(4, 4), Item.INVENTORY_COMPASS_FISHER.getItem(user.user()));
    }

}
