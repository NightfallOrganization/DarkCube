/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.CapsulePerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ListenerCapsule extends BasicPerkListener {

	public ListenerCapsule() {
		super(CapsulePerk.CAPSULE);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		Player p = perk.owner().getBukkitEntity();
		Location loc = p.getLocation();
		this.setBlock(perk.owner(), loc.subtract(0, 1, 0));
		this.setBlock(perk.owner(), loc.add(0, 3, 0));
		this.setBlock2(perk.owner(), loc.subtract(1, 1, 0));
		this.setBlock2(perk.owner(), loc.subtract(0, 1, 0));
		this.setBlock2(perk.owner(), loc.add(2, 1, 0));
		this.setBlock2(perk.owner(), loc.subtract(0, 1, 0));
		this.setBlock2(perk.owner(), loc.subtract(1, 0, 1));
		this.setBlock2(perk.owner(), loc.add(0, 1, 0));
		this.setBlock2(perk.owner(), loc.add(0, 0, 2));
		this.setBlock2(perk.owner(), loc.subtract(0, 1, 0));
		p.teleport(p.getLocation().getBlock().getLocation().add(.5, .25, .5)
				.setDirection(p.getLocation().getDirection()));
		return true;
	}

	private void setBlock(WBUser user, Location block) {
		WoolBattle.getInstance().getIngame().place(user, block.getBlock(), 2, false);
	}

	private void setBlock2(WBUser user, Location block) {
		WoolBattle.getInstance().getIngame().place(user, block.getBlock(), 0, false);
	}

}
