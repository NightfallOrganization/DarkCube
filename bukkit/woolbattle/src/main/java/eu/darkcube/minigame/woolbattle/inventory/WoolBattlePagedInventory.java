/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.inventory;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.system.inventoryapi.v1.AsyncPagedInventory;
import eu.darkcube.system.inventoryapi.v1.DefaultAsyncPagedInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.inventoryapi.v1.PageArrow;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

import java.util.Collection;

public abstract class WoolBattlePagedInventory extends DefaultAsyncPagedInventory {
	protected final WBUser user;
	private boolean done = false;

	public WoolBattlePagedInventory(InventoryType inventoryType, Component title, WBUser user) {
		this(inventoryType, title, 5 * 9, AsyncPagedInventory.box(2, 2, 4, 8), user);
	}

	public WoolBattlePagedInventory(InventoryType inventoryType, Component title, int size,
			int[] box, WBUser user) {
		super(inventoryType, title, size, box, () -> true);
		this.user = user;
		this.complete();
	}

	@Override
	protected void offerAnimations(Collection<AnimationInformation> informations) {
		// We want everything synced and on main thread
		asyncOfferAnimations(informations);
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
		arrowItem.put(PageArrow.PREVIOUS, Item.PREV_PAGE.getItem(user));
		arrowItem.put(PageArrow.NEXT, Item.NEXT_PAGE.getItem(user));
		super.insertArrowItems();
	}

	protected void insertDefaultItems0() {
		super.insertDefaultItems();
	}
}
