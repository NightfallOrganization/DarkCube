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