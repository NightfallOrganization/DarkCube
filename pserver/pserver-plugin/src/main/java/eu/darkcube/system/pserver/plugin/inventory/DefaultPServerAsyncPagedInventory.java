/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.inventory;

import eu.darkcube.system.inventoryapi.v1.DefaultAsyncPagedInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.inventoryapi.v1.PageArrow;
import eu.darkcube.system.pserver.plugin.Item;
import eu.darkcube.system.pserver.plugin.user.User;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DefaultPServerAsyncPagedInventory extends DefaultAsyncPagedInventory {

	public final User user;

	protected final Deque<Condition> conditions;

	private boolean apply = false;

	public DefaultPServerAsyncPagedInventory(User user, InventoryType inventoryType, String title) {
		// TODO: Instant to animations
		super(inventoryType, title, () -> true);
		this.user = user;
		this.conditions = new ConcurrentLinkedDeque<>();
		this.addConditions(this.conditions);
		this.tryFillInv();
	}

	protected void tryFillInv() {
		this.insertDefaultItems();
		this.offerAnimations(this.informations);
	}

	protected void addConditions(Deque<Condition> conditions) {
		this.conditions.offer(() -> this.user != null);
	}

	protected boolean conditionsApply() {
		if (this.apply) {
			return true;
		}
		if (this.conditions != null) {
			for (Condition condition : this.conditions) {
				if (!condition.applies()) {
					return false;
				}
			}
			this.apply = true;
			return true;
		}
		return false;
	}

	public User getUser() {
		return this.user;
	}

	@Override
	protected void offerAnimations(Collection<AnimationInformation> informations) {
		if (this.conditionsApply()) {
			super.offerAnimations(informations);
		}
	}

	@Override
	protected void insertDefaultItems() {
		if (this.conditionsApply()) {
			super.insertDefaultItems();
		}
	}

	@Override
	protected void insertArrowItems() {
		if (this.conditionsApply()) {
			this.arrowItem.put(PageArrow.PREVIOUS, Item.ARROW_PREVIOUS.getItem(this.user));
			this.arrowItem.put(PageArrow.NEXT, Item.ARROW_NEXT.getItem(this.user));
			super.insertArrowItems();
		}
	}

	public void open() {
		this.open(this.user.getOnlinePlayer());
	}

	public static interface Condition {

		boolean applies();

	}

}
