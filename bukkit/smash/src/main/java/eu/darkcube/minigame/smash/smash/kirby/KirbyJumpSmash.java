/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.smash.kirby;

import eu.darkcube.minigame.smash.smash.JumpSmash;
import eu.darkcube.minigame.smash.api.user.User;

public class KirbyJumpSmash extends JumpSmash {

	public KirbyJumpSmash() {
		super(5);
	}

	@Override
	protected void applyKnockback(User user) {
		applyDefaultKnockback(user);
	}
}
