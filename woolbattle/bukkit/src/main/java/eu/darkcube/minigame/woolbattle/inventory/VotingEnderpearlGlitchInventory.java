/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.inventory;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.Vote;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static eu.darkcube.system.inventoryapi.item.ItemBuilder.item;

public class VotingEnderpearlGlitchInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE =
            InventoryType.of("woolbattle-voting-enderpearl-glitch");

    public VotingEnderpearlGlitchInventory(WoolBattle woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.INVENTORY_VOTING_EP_GLITCH.getMessage(user), user);
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null)
            return;
        String itemId = ItemManager.getItemId(event.item());
        if (itemId == null)
            return;
        Boolean vote = null;
        if (itemId.equals(Item.GENERAL_VOTING_FOR.getItemId())) {
            vote = true;
        } else if (itemId.equals(Item.GENERAL_VOTING_AGAINST.getItemId())) {
            vote = false;
        }
        Vote<Boolean> old = woolbattle.lobby().VOTES_EP_GLITCH.get(user);
        if (old != null) {
            if (old.vote == vote) {
                user.user().sendMessage(Message.ALREADY_VOTED_FOR_THIS);
                return;
            }
        }
        if (vote != null) {
            woolbattle.lobby().VOTES_EP_GLITCH.put(user, new Vote<>(System.currentTimeMillis(), vote));
            woolbattle.lobby().recalculateEpGlitch();
            user.user().sendMessage(
                    vote ? Message.VOTED_FOR_EP_GLITCH : Message.VOTED_AGAINST_EP_GLITCH);
            recalculate();
        }
    }

    @Override
    protected void fillItems(Map<Integer, ItemStack> items) {
        ItemBuilder b1 = item(Item.GENERAL_VOTING_FOR.getItem(user));
        ItemBuilder b2 = item(Item.GENERAL_VOTING_AGAINST.getItem(user));
        Vote<Boolean> vote = WoolBattle.instance().lobby().VOTES_EP_GLITCH.get(user);
        if (vote != null) {
            if (vote.vote) {
                b1.glow(true);
            } else {
                b2.glow(true);
            }
        }
        fallbackItems.put(IInventory.slot(3, 4), b1.build());
        fallbackItems.put(IInventory.slot(3, 6), b2.build());
        updateSlots.offer(IInventory.slot(3, 4));
        updateSlots.offer(IInventory.slot(3, 6));
    }

    @Override
    protected void insertFallbackItems() {
        fallbackItems.put(IInventory.slot(1, 5), Item.LOBBY_VOTING_EP_GLITCH.getItem(user));
        super.insertFallbackItems();
    }
}
