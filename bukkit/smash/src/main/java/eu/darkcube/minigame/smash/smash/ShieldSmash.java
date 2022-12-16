/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.smash;

import org.bukkit.Color;

import eu.darkcube.minigame.smash.api.user.User;

public class ShieldSmash extends Smash {

	private Color shieldColor;

	public ShieldSmash() {
		this(Color.LIME);
	}

	public ShieldSmash(Color color) {
		super(5);
		this.shieldColor = color;
	}

	public Color getShieldColor() {
		return shieldColor;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected final void execute0(User user) {
		if (user.getPlayer().isOnGround()) {
			user.setShieldActive(!user.isShieldActive());
			if (user.isShieldActive()) {
				startShield(user);
			} else {
				stopShield(user);
			}
		}
	}

	protected void startShield(User user) {

	}

	protected void stopShield(User user) {

	}
}
