/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ListenerDoublejump extends BaseListener {

	private Map<Player, Integer> cooldown = new HashMap<>();
	private Set<Player> waiting = new HashSet<>();

	@EventHandler
	public void handle(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(p));
		if (user.isBuildMode()) {
			return;
		}
		if (!e.isFlying())
			return;
		if (user.getCurrentJaR() != null)
			return;
		e.setCancelled(true);
		if (!this.cooldown.containsKey(p)) {
			this.cooldown.put(p, 50);
			Vector v = p.getLocation().getDirection();
			v.normalize().multiply(3);
			v.setY(Math.min(2, Math.max(1, v.getY())));
			p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 10, 1);
			p.setVelocity(v);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!ListenerDoublejump.this.cooldown.containsKey(p)) {
						p.setAllowFlight(true);
						this.cancel();
						return;
					}
					if (p.isFlying())
						return;
					ListenerDoublejump.this.cooldown.put(p,
							ListenerDoublejump.this.cooldown.get(p) - 1);
					p.setExp(1 - (ListenerDoublejump.this.cooldown.get(p) / 80F));
					if (ListenerDoublejump.this.cooldown.get(p) <= 0) {
						ListenerDoublejump.this.cooldown.remove(p);
						ListenerDoublejump.this.waiting.remove(p);
					}
				}
			}.runTaskTimer(Lobby.getInstance(), 1, 1);
		} else if (!this.waiting.contains(p)) {
			this.cooldown.put(p, this.cooldown.get(p) + 50);
			if (this.cooldown.get(p) > 80)
				this.waiting.add(p);
			Vector v = p.getLocation().getDirection();
			v.normalize().multiply(3);
			v.setY(Math.min(2, Math.max(1, v.getY())));
			p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 10, 1);
			p.setVelocity(v);
		}
		if (this.waiting.contains(p)) {
			p.setAllowFlight(false);
		}
	}
}
