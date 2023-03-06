/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive;

import eu.darkcube.minigame.woolbattle.event.perk.passive.EventEnderPearl;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.ElevatorPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

public class ListenerElevator implements Listener {
	@EventHandler
	public void handle(EventEnderPearl event) {
		WBUser user = event.user();
		Collection<UserPerk> perks = user.perks().perks(ElevatorPerk.ELEVATOR);
		if (perks.isEmpty())
			return;
		for (UserPerk perk : perks) {
			if (perk.cooldown() == 0) {
				if (event.canElevate()) {
					event.elevate(true);
					perk.cooldown(perk.perk().cooldown().ticks());
				}
			} else {
				perk.cooldown(perk.cooldown() - 1);
			}
		}
	}
}
