package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerBooster extends BasicPerkListener {

	public ListenerBooster() {
		super(PerkType.BOOSTER);
	}

	@Override
	protected boolean activateRight(User user, Perk perk) {
		Player p = user.getBukkitEntity();
		Vector velo = p.getLocation().getDirection()
				.setY(p.getLocation().getDirection().getY() + 0.3).multiply(2.7);
		velo.setY(velo.getY() / 1.8);
		p.setVelocity(velo);
		return true;
	}

}
