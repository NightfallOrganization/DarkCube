/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.inventory;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;
import eu.darkcube.system.bukkit.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static eu.darkcube.system.bukkit.inventoryapi.item.ItemBuilder.item;

public class SettingsWoolDirectionInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle-settings-wool-direction");

    public SettingsWoolDirectionInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.WOOL_DIRECTION_SETTINGS_TITLE.getMessage(user), user);
    }

    @Override
    protected void fillItems(Map<Integer, ItemStack> items) {
        ItemBuilder ltr = item(Item.SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT.getItem(user));
        ItemBuilder rtl = item(Item.SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT.getItem(user));
        WoolSubtractDirection dir = user.woolSubtractDirection();
        if (dir == WoolSubtractDirection.LEFT_TO_RIGHT) {
            ltr.glow(true);
        } else if (dir == WoolSubtractDirection.RIGHT_TO_LEFT) {
            rtl.glow(true);
        }
        fallbackItems.put(IInventory.slot(3, 4), ltr.build());
        fallbackItems.put(IInventory.slot(3, 6), rtl.build());
        updateSlots.offer(IInventory.slot(3, 4));
        updateSlots.offer(IInventory.slot(3, 6));
    }

    @Override
    protected void insertFallbackItems() {
        super.insertFallbackItems();
        fallbackItems.put(IInventory.slot(1, 5), Item.SETTINGS_WOOL_DIRECTION.getItem(user));
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
    }
}
