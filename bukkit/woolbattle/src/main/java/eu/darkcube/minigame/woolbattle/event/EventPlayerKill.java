/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.darkcube.minigame.woolbattle.user.User;

public class EventPlayerKill extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private User user;
	private User killer;
	private boolean cancel;

	public EventPlayerKill(User user, User killer) {
		this.user = user;
		this.killer = killer;
		cancel = false;
	}

	public User getKiller() {
		return killer;
	}

	public User getUser() {
		return user;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
