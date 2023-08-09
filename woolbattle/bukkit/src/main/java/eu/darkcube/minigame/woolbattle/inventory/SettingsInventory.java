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
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;

public class SettingsInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle-settings");

    public SettingsInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.SETTINGS_TITLE.getMessage(user), user);
    }

    @Override
    protected void insertFallbackItems() {
        super.insertFallbackItems();
        fallbackItems.put(IInventory.slot(1, 5), Item.SETTINGS.getItem(user));
        fallbackItems.put(IInventory.slot(3, 4), Item.SETTINGS_WOOL_DIRECTION.getItem(user));
        fallbackItems.put(IInventory.slot(3, 6), Item.SETTINGS_HEIGHT_DISPLAY.getItem(user));
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null)
            return;
        String itemId = ItemManager.getItemId(event.item());
        if (itemId == null)
            return;
        if (itemId.equals(Item.SETTINGS_HEIGHT_DISPLAY.getItemId())) {
            user.setOpenInventory(new SettingsHeightDisplayInventory(woolbattle, user));
        } else if (itemId.equals(Item.SETTINGS_WOOL_DIRECTION.getItemId())) {
            user.setOpenInventory(new SettingsWoolDirectionInventory(woolbattle, user));
        }
    }
}
