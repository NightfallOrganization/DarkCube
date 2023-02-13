/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.darkcube.minigame.woolbattle.user.WBUser;

public class EventPlayerDeath extends Event {

	private static final HandlerList handlers = new HandlerList();

	private WBUser user;

	public EventPlayerDeath(WBUser user) {
		this.user = user;
	}

	public WBUser getUser() {
		return user;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
