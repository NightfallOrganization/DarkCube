/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.SafetyPlatformPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Location;

public class ListenerSafetyPlatform extends BasicPerkListener {

	public ListenerSafetyPlatform() {
		super(SafetyPlatformPerk.SAFETY_PLATFORM);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		boolean suc = setBlocks(perk.owner());
		if (!suc)
			Ingame.playSoundNotEnoughWool(perk.owner());
		return suc;
	}

	private boolean setBlocks(WBUser p) {

		final Location pLoc = p.getBukkitEntity().getLocation();
		Location center =
				pLoc.getBlock().getLocation().add(0.5, 0.25, 0.5).setDirection(pLoc.getDirection());
		center.subtract(-0.5, 0, -0.5);
		final double radius = 2.5;
		for (double x = -radius; x <= radius; x++) {
			for (double z = -radius; z <= radius; z++) {
				if (isCorner(x, z, radius)) {
					continue;
				}
				block(center.clone().add(x, -1, z), p);
			}
		}

		p.getBukkitEntity().teleport(center);
		return true;
	}

	private boolean isCorner(double x, double z, @SuppressWarnings("SameParameterValue") double r) {
		return (x == -r && z == -r) || (x == r && z == -r) || (x == -r && z == r) || (x == r
				&& z == r);
	}

	private void block(Location loc, WBUser u) {
		WoolBattle.getInstance().getIngame().place(u, loc.getBlock());
	}
}
