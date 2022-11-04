package eu.darkcube.minigame.smash.smash.kirby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.smash.smash.FrontSmash;
import eu.darkcube.minigame.smash.api.SmashAPI;
import eu.darkcube.minigame.smash.api.user.User;
import eu.darkcube.minigame.smash.util.MathUtil;

public class KirbyFrontSmash extends FrontSmash {

	public KirbyFrontSmash() {
		super(30);
	}

	@Override
	public void execute0(User user) {
		Player p = user.getPlayer();
		for (Player t : Bukkit.getOnlinePlayers()) {
			boolean attack = MathUtil.canAttack(p, t, 3, 1, 40);
			if (attack) {
				User tuser = SmashAPI.getApi().getUser(t);
				tuser.damage(3);
				tuser.stun(40);
				t.setVelocity(p.getLocation().getDirection().setY(0).normalize().multiply(.2).setY(0.3));
			}
		}
	}
}
