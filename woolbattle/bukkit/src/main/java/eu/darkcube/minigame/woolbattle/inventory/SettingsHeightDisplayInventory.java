/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.inventory;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SettingsHeightDisplayInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle-settings-height-display");

    public SettingsHeightDisplayInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.HEIGHT_DISPLAY_SETTINGS_TITLE.getMessage(user), user);
    }

    @Override
    protected void fillItems(Map<Integer, ItemStack> items) {
        fallbackItems.put(IInventory.slot(3, 4), Item.SETTINGS_HEIGHT_DISPLAY_COLOR.getItem(user));
        fallbackItems.put(IInventory.slot(3, 6), (user.heightDisplay().isEnabled()
                ? Item.HEIGHT_DISPLAY_ON
                : Item.HEIGHT_DISPLAY_OFF).getItem(user));
        updateSlots.offer(IInventory.slot(3, 4));
        updateSlots.offer(IInventory.slot(3, 6));
    }

    @Override
    protected void insertFallbackItems() {
        super.insertFallbackItems();
        fallbackItems.put(IInventory.slot(1, 5), Item.SETTINGS_HEIGHT_DISPLAY.getItem(user));
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null)
            return;
        String itemId = ItemManager.getItemId(event.item());
        if (itemId == null)
            return;
        if (itemId.equals(Item.SETTINGS_HEIGHT_DISPLAY_COLOR.getItemId())) {
            user.setOpenInventory(new SettingsHeightDisplayColorInventory(woolbattle, user));
        } else if (itemId.equals(Item.HEIGHT_DISPLAY_OFF.getItemId()) || itemId.equals(
                Item.HEIGHT_DISPLAY_ON.getItemId())) {
            HeightDisplay heightDisplay = user.heightDisplay();
            heightDisplay.setEnabled(!heightDisplay.isEnabled());
            user.heightDisplay(heightDisplay);
            recalculate();
        }
    }
}
