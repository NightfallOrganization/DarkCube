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

public class ListenerRocketJump extends PerkListener {
	public ListenerRocketJump(Perk perk) {
		super(perk);
	}

	@EventHandler
	public void handle(EventDoubleJump event) {
		for (UserPerk ignored : event.user().perks().perks(perk().perkName())) {
			Vector velocity = event.velocity();
			velocity.setY(velocity.getY() * 1.45);
			event.velocity(velocity);
		}
	}
}
