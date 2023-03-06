/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.misc;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import org.bukkit.entity.EnderPearl;
import org.bukkit.metadata.FixedMetadataValue;

public class ListenerEnderpearl extends BasicPerkListener {
	public ListenerEnderpearl(Perk perk) {
		super(perk);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		EnderPearl enderPearl = perk.owner().getBukkitEntity().launchProjectile(EnderPearl.class);
		enderPearl.setMetadata("perk",
				new FixedMetadataValue(WoolBattle.getInstance(), perk.perk().perkName().getName()));
		return true;
	}
}
