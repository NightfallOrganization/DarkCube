/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.event.user;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.HandlerList;

public class EventUserMayAttack extends UserEvent {
	private static final HandlerList handlers = new HandlerList();
	private boolean mayAttack;

	public EventUserMayAttack(WBUser user, boolean mayAttack) {
		super(user);
		this.mayAttack = mayAttack;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean mayAttack() {
		return mayAttack;
	}

	public void mayAttack(boolean mayAttack) {
		this.mayAttack = mayAttack;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
