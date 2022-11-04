package eu.darkcube.minigame.smash.smash;

import org.bukkit.entity.Player;

import eu.darkcube.minigame.smash.api.user.User;

public abstract class JumpSmash extends Smash {

	private int maxUses;

	public JumpSmash(int maxUses) {
		super(2);
		this.maxUses = maxUses;
	}

	protected abstract void applyKnockback(User user);

	protected final void applyDefaultKnockback(User user) {
		Player p = user.getPlayer();
		p.setVelocity(p.getLocation().getDirection().setY(0).normalize().multiply(.5).setY(0.7));
	}

	public int getMaxUses() {
		return maxUses;
	}

	@Override
	protected final void execute0(User user) {
		if (user.getJumpUses() < maxUses) {
			user.setJumpUses(user.getJumpUses() + 1);
			applyKnockback(user);
		}
	}
}
