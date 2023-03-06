/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive;

import eu.darkcube.minigame.woolbattle.event.perk.EventDoubleJump;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.PerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class ListenerLongJump extends PerkListener {

	public ListenerLongJump(Perk perk) {
		super(perk);
	}

	@EventHandler
	public void handle(EventDoubleJump event) {
		for (UserPerk ignored : event.user().perks().perks(perk().perkName())) {
			Vector velocity = event.velocity();
			double y = velocity.getY();
			velocity = velocity.multiply(5.2);
			velocity.setY(y * 1.06);
		}
	}
}
