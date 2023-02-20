/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.BoosterPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ListenerBooster extends BasicPerkListener {

	public ListenerBooster() {
		super(BoosterPerk.BOOSTER);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		Player p = perk.owner().getBukkitEntity();
		Vector velo =
				p.getLocation().getDirection().setY(p.getLocation().getDirection().getY() + 0.3)
						.multiply(2.7);
		velo.setY(velo.getY() / 1.8);
		p.setVelocity(velo);
		return true;
	}

}
