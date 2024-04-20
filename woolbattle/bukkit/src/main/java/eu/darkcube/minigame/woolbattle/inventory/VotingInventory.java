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
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;

public class VotingInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle-voting");

    public VotingInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.INVENTORY_VOTING.getMessage(user), user);
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        String itemId = ItemManager.getItemId(event.item());
        if (itemId == null) return;
        if (itemId.equals(Item.LOBBY_VOTING_EP_GLITCH.getItemId())) {
            user.setOpenInventory(new VotingEnderpearlGlitchInventory(woolbattle, user));
        } else if (itemId.equals(Item.LOBBY_VOTING_MAPS.getItemId())) {
            user.setOpenInventory(new VotingMapsInventory(woolbattle, user));
        } else if (itemId.equals(Item.LOBBY_VOTING_LIFES.getItemId())) {
            user.setOpenInventory(new VotingLifesInventory(woolbattle, user));
        }
    }

    @Override
    protected void insertFallbackItems() {
        super.insertFallbackItems();
        fallbackItems.put(IInventory.slot(1, 5), Item.LOBBY_VOTING.getItem(user));
        fallbackItems.put(IInventory.slot(3, 3), Item.LOBBY_VOTING_EP_GLITCH.getItem(user));
        fallbackItems.put(IInventory.slot(3, 5), Item.LOBBY_VOTING_MAPS.getItem(user));
        fallbackItems.put(IInventory.slot(3, 7), Item.LOBBY_VOTING_LIFES.getItem(user));
    }
}
