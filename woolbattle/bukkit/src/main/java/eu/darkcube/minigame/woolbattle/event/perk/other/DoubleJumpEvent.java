/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.event.perk.other;

import eu.darkcube.minigame.woolbattle.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class DoubleJumpEvent extends UserEvent {
	private static final HandlerList handlers = new HandlerList();
	private Vector velocity;

	public DoubleJumpEvent(WBUser user, Vector velocity) {
		super(user);
		this.velocity = velocity;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Vector velocity() {
		return velocity.clone();
	}

	public void velocity(Vector velocity) {
		this.velocity = velocity;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
