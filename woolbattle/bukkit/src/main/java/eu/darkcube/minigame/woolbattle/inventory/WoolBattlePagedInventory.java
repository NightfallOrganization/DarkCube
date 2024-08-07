/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.inventory;

import java.util.Collection;
import java.util.HashSet;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.system.bukkit.inventoryapi.v1.AsyncPagedInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.DefaultAsyncPagedInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.bukkit.inventoryapi.v1.PageArrow;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public abstract class WoolBattlePagedInventory extends DefaultAsyncPagedInventory {
    protected final WBUser user;
    protected final WoolBattleBukkit woolbattle;
    private boolean done = false;

    public WoolBattlePagedInventory(WoolBattleBukkit woolbattle, InventoryType inventoryType, Component title, WBUser user) {
        this(woolbattle, inventoryType, title, 5 * 9, AsyncPagedInventory.box(2, 2, 4, 8), user);
    }

    public WoolBattlePagedInventory(WoolBattleBukkit woolbattle, InventoryType inventoryType, Component title, int size, int[] box, WBUser user) {
        super(inventoryType, title, size, box, () -> true);
        this.woolbattle = woolbattle;
        this.user = user;
        this.complete();
    }

    @Override
    protected final void offerAnimations(Collection<AnimationInformation> informations) {
        // We want everything synced and on main thread
        asyncOfferAnimations(informations);
    }

    @Override
    protected final void asyncOfferAnimations(Collection<AnimationInformation> information) {
        if (done()) {
            super.asyncOfferAnimations(information);
        }
    }

    @Override
    protected void calculateArrows() {
        boolean oldEmpty = enabledArrows.isEmpty();
        for (PageArrow arrow : new HashSet<>(this.enabledArrows)) {
            this.hideArrow(arrow);
        }
        final int page = this.getPage();
        final int maxSlot = this.items.keySet().stream().mapToInt(i -> i).max().orElse(0);
        final int maxPage = maxSlot / this.pageSize + 1;
        if (maxPage > page) {
            this.showArrow(PageArrow.NEXT);
        }
        if (page > 1) {
            this.showArrow(PageArrow.PREVIOUS);
        }
        if (oldEmpty != enabledArrows.isEmpty()) {
            if (!enabledArrows.isEmpty()) {
                staticItems.put(IInventory.slot(2, 1), Item.PREV_PAGE_UNUSABLE.getItem(user));
                staticItems.put(IInventory.slot(3, 1), Item.PREV_PAGE_UNUSABLE.getItem(user));
                staticItems.put(IInventory.slot(4, 1), Item.PREV_PAGE_UNUSABLE.getItem(user));
                staticItems.put(IInventory.slot(2, 9), Item.NEXT_PAGE_UNUSABLE.getItem(user));
                staticItems.put(IInventory.slot(3, 9), Item.NEXT_PAGE_UNUSABLE.getItem(user));
                staticItems.put(IInventory.slot(4, 9), Item.NEXT_PAGE_UNUSABLE.getItem(user));
            } else {
                for (int slot : arrowSlots.get(PageArrow.PREVIOUS)) {
                    staticItems.remove(slot);
                }
                for (int slot : arrowSlots.get(PageArrow.NEXT)) {
                    staticItems.remove(slot);
                }
            }
            updateSlots.addAll(Arrays.asList(arrowSlots.get(PageArrow.PREVIOUS)));
            updateSlots.addAll(Arrays.asList(arrowSlots.get(PageArrow.NEXT)));
        }
    }

    protected void complete() {
        this.done = true;
        this.insertDefaultItems();
        this.offerAnimations(this.informations);
    }

    public WBUser user() {
        return user;
    }

    protected boolean done() {
        return done;
    }

    @Override
    protected final void insertDefaultItems() {
        if (done()) {
            super.insertDefaultItems();
        }
    }

    @Override
    protected void playSound() {
    }

    @Override
    protected void insertArrowItems() {
        arrowSlots.putIfAbsent(PageArrow.PREVIOUS, new Integer[]{IInventory.slot(2, 1), IInventory.slot(3, 1), IInventory.slot(4, 1)});
        arrowSlots.putIfAbsent(PageArrow.NEXT, new Integer[]{IInventory.slot(2, 9), IInventory.slot(3, 9), IInventory.slot(4, 9)});
        arrowItem.put(PageArrow.PREVIOUS, Item.PREV_PAGE.getItem(user));
        arrowItem.put(PageArrow.NEXT, Item.NEXT_PAGE.getItem(user));
        super.insertArrowItems();
    }

    @Override
    protected void insertFallbackItems() {
        super.insertFallbackItems();
    }

    protected void insertDefaultItems0() {
        super.insertDefaultItems();
    }
}
