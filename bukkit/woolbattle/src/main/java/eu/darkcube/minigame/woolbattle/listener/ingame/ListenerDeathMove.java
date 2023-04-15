/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerGhost;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerDeathMove extends Listener<PlayerMoveEvent> {

	private WoolBattle main = WoolBattle.instance();

	@Override
	@EventHandler
	public void handle(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		WBUser user = WBUser.getUser(p);
		if (user.getTeam().getType() != TeamType.SPECTATOR) {
			if (p.getLocation().getY() <= this.main.getMap().getDeathHeight()) {
				if (ListenerGhost.isGhost(user)) {
					ListenerGhost.reset(user);
					return;
				}
				if (user.getTicksAfterLastHit() <= 200) {
					this.main.getIngame().kill(user);
				} else {
					p.teleport(user.getTeam().getSpawn());
				}
			}
			return;
		}
		if (e.getTo().getY() < 0) {
			p.teleport(user.getTeam().getSpawn());
		}
	}

}
