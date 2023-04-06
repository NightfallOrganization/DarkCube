/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.event.perk.passive;

import eu.darkcube.minigame.woolbattle.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class EventMayDoubleJump extends UserEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final UserPerk ownerPerk;
	private boolean mayDoubleJump;
	private boolean cancel;

	public EventMayDoubleJump(UserPerk ownerPerk, boolean mayDoubleJump) {
		super(ownerPerk.owner());
		this.ownerPerk = ownerPerk;
		this.mayDoubleJump = mayDoubleJump;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public UserPerk ownerPerk() {
		return ownerPerk;
	}

	public boolean mayDoubleJump() {
		return mayDoubleJump;
	}

	public void mayDoubleJump(boolean mayDoubleJump) {
		this.mayDoubleJump = mayDoubleJump;
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
