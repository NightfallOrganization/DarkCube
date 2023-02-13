/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive;

import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerGhost;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.LongJumpPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.RocketJumpPerk;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class ListenerDoubleJump extends Listener<PlayerToggleFlightEvent> {
	public static final int COOLDOWN = 65;
	public static final int COST = 5;
	public Map<Player, ObservableInteger> cooldown = new HashMap<>();

	@Override
	@EventHandler
	public void handle(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		WBUser user = WBUser.getUser(p);
		if (p.getGameMode() != GameMode.SURVIVAL || user.isTrollMode()
				|| user.getTeam().getType() == TeamType.SPECTATOR) {
			return;
		}
		if (e.isFlying())
			e.setCancelled(true);
		if (this.refresh(p) && e.isFlying()) {
			this.cooldown.get(p).setObject(ListenerDoubleJump.COOLDOWN);
			Vector velo = p.getLocation().getDirection();
			velo.setY(0).normalize().multiply(0.1);
			// velo.multiply(0.1);
			double heightMultiplier =
					Math.pow(1.45, user.perks().count(RocketJumpPerk.ROCKET_JUMP));
			for (int i = 0; i < user.perks().count(LongJumpPerk.LONG_JUMP); i++) {
				velo.multiply(5.3);
				heightMultiplier *= 1.08;
			}
			velo.setY(1.05 * heightMultiplier);
			p.setAllowFlight(false);
			p.setVelocity(velo);
			ItemManager.removeItems(user, p.getInventory(),
					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColorByte()),
					ListenerDoubleJump.COST);
			new Scheduler() {
				@Override
				public void run() {
					if (ListenerDoubleJump.this.cooldown.get(p).getObject() == 0) {
						p.setAllowFlight(true);
						this.cancel();
						return;
					}
					ListenerDoubleJump.this.cooldown.get(p)
							.setObject(ListenerDoubleJump.this.cooldown.get(p).getObject() - 1);
				}
			}.runTaskTimer(0, 1);
		}
	}

	public boolean refresh(Player p) {
		if (ListenerGhost.isGhost(WBUser.getUser(p))) {
			if (p.getAllowFlight())
				p.setAllowFlight(false);
			return false;
		}

		if (!this.cooldown.containsKey(p)) {
			this.cooldown.put(p, new SimpleObservableInteger(0) {
				@Override
				public void onChange(ObservableObject<Integer> instance, Integer oldValue,
						Integer newValue) {
					p.setFoodLevel((int) ((ListenerDoubleJump.COOLDOWN - newValue) * 20F
							/ ListenerDoubleJump.COOLDOWN));
				}

				@Override
				public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue,
						Integer newValue) {
				}
			});
		}
		if (p.getGameMode() == GameMode.SURVIVAL) {
			ObservableInteger cdi = this.cooldown.get(p);
			if (ItemManager.countItems(Material.WOOL, p.getInventory()) >= ListenerDoubleJump.COST
					&& cdi.getObject() == 0) {
				if (!p.getAllowFlight()) {
					p.setAllowFlight(true);
				}
			} else {
				if (p.getAllowFlight()) {
					p.setAllowFlight(false);
				}
			}
			return p.getAllowFlight();
		}
		p.setAllowFlight(true);
		return true;
	}
}
