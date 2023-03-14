/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ListenerRope extends BasicPerkListener {

	public ListenerRope(Perk perk) {
		super(perk);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		Player p = perk.owner().getBukkitEntity();
		Vector vec = p.getLocation().getDirection().setY(0).normalize();
		double ax = Math.abs(vec.getX());
		double az = Math.abs(vec.getZ());
		if (ax > az) {
			vec.setZ(0);
			vec.normalize();
		} else {
			vec.setX(0);
			vec.normalize();
		}
		Location loc = p.getLocation().add(vec).add(0, 1, 0);

		for (int i = 0; i < 10; i++) {
			loc = loc.subtract(0, 1, 0);
			setBlock(loc, perk.owner());
		}

		p.teleport(p.getLocation().getBlock().getLocation().add(.5, .25, .5)
				.setDirection(p.getLocation().getDirection()));

		return true;
	}

	private void setBlock(Location block, WBUser user) {
		WoolBattle.instance().getIngame().place(user, block.getBlock());
	}
}
