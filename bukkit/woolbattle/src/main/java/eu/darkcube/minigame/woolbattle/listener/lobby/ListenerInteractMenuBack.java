/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.lobby;

import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ListenerInteractMenuBack extends Listener<EventInteract> {

	@Override
	@EventHandler(priority = EventPriority.LOW)
	public void handle(EventInteract e) {
		Player p = e.getPlayer();
		WBUser user = WBUser.getUser(p);
		//		if (!e.getItem().hasItemMeta() && e.getClick() == ClickType.RIGHT) {
		//			if (user.getOpenInventory() != null) {
		//				switch (user.getOpenInventory()) {
		//					e.setCancelled(true);
		//					WoolBattle.getInstance().getLobby().listenerItemPerks.openInventory(p, user);
		//					break;
		//				case TEAMS:
		//				case VOTING:
		//				case GADGETS:
		//				case PERKS:
		//				case COMPASS_TELEPORT:
		//					break;
		//				case VOTING_EP_GLITCH:
		//				case VOTING_MAP:
		//				case VOTING_LIFES:
		//					e.setCancelled(true);
		//					WoolBattle.getInstance().getLobby().listenerItemVoting.openInventory(p, user);
		//					break;
		//				default:
		//					break;
		//				}
		//			}
		//		}
	}
}
