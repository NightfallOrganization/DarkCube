/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.event.user;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when the woolcount of a user increases
 */
public class EventUserAddWool extends UserEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private int amount;
	private boolean dropRemaining;

	public EventUserAddWool(WBUser user, int amount, boolean dropRemaining) {
		super(user);
		this.amount = amount;
		this.dropRemaining = dropRemaining;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public int amount() {
		return amount;
	}

	public void amount(int amount) {
		this.amount = amount;
	}

	public boolean dropRemaining() {
		return dropRemaining;
	}

	public void dropRemaining(boolean dropRemaining) {
		this.dropRemaining = dropRemaining;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancel = b;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
