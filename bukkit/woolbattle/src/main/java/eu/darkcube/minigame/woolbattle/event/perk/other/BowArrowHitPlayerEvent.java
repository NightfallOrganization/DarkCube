/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.event.perk.other;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BowArrowHitPlayerEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Arrow arrow;
	private final WBUser shooter;
	private final WBUser target;
	private boolean cancel = false;

	public BowArrowHitPlayerEvent(Arrow arrow, WBUser shooter, WBUser target) {
		this.arrow = arrow;
		this.shooter = shooter;
		this.target = target;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Arrow arrow() {
		return arrow;
	}

	public WBUser shooter() {
		return shooter;
	}

	public WBUser target() {
		return target;
	}

	@Override
	public HandlerList getHandlers() {
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
