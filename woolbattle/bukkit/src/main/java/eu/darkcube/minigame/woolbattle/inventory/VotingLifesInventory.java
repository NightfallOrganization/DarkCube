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
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.util.data.Key;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class VotingLifesInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle_voting_lifes");
    private static final Key LIFES = new Key(WoolBattleBukkit.instance(), "voting_lifes");

    public VotingLifesInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.INVENTORY_VOTING_LIFES.getMessage(user), user);
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null)
            return;
        String stringLifes = ItemManager.getId(event.item(), LIFES);
        if (stringLifes == null)
            return;
        int lifes = Integer.parseInt(stringLifes);
        WoolBattleBukkit.instance().lobby().VOTES_LIFES.put(user, lifes);
        user.user().sendMessage(Message.VOTED_LIFES, lifes);
        recalculate();
    }

    @Override
    protected void fillItems(Map<Integer, ItemStack> items) {
        set(IInventory.slot(3, 3), 3);
        set(IInventory.slot(3, 4), 10);
        set(IInventory.slot(3, 6), 20);
        set(IInventory.slot(3, 7), 30);
    }

    @Override
    protected void insertFallbackItems() {
        fallbackItems.put(IInventory.slot(1, 5), Item.LOBBY_VOTING_LIFES.getItem(user));
        super.insertFallbackItems();
    }

    private void set(int slot, int lifes) {
        ItemBuilder builder = ItemBuilder.item(Item.LOBBY_VOTING_LIFES_ENTRY.getItem(user, lifes));
        ItemManager.setId(builder, LIFES, String.valueOf(lifes));
        int lifeVotes = -1;
        if (WoolBattleBukkit.instance().lobby().VOTES_LIFES.containsKey(user)) {
            lifeVotes = WoolBattleBukkit.instance().lobby().VOTES_LIFES.get(user);
        }
        if (lifeVotes == lifes) {
            builder.glow(true);
        }
        fallbackItems.put(slot, builder.build());
        updateSlots.offer(slot);
    }
}
