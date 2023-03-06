/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive;

import eu.darkcube.minigame.woolbattle.event.perk.EventDoubleJump;
import eu.darkcube.minigame.woolbattle.event.perk.active.EventGhostStateChange;
import eu.darkcube.minigame.woolbattle.event.perk.passive.EventMayDoubleJump;
import eu.darkcube.minigame.woolbattle.event.user.EventUserWoolCountUpdate;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.PerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.perks.other.DoubleJumpPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class ListenerDoubleJump extends PerkListener {

	public ListenerDoubleJump(Perk perk) {
		super(perk);
	}

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

		UserPerk perk = refresh(user);
		if (perk != null && e.isFlying()) {
			perk.cooldown(perk.perk().cooldown().ticks());
			Vector velo =
					p.getLocation().getDirection().setY(0).normalize().multiply(0.11).setY(1.05);

			EventDoubleJump event = new EventDoubleJump(user, velo);
			Bukkit.getPluginManager().callEvent(event);

			p.setVelocity(event.velocity());
			payForThePerk(user, perk());
		}
	}

	@EventHandler
	public void handle(EventUserWoolCountUpdate event) {
		refresh(event.user());
	}

	@EventHandler
	public void handle(EventGhostStateChange event) {
		refresh(event.user());
	}

	@EventHandler
	public void handle(PlayerGameModeChangeEvent e) {
		new Scheduler(() -> refresh(WBUser.getUser(e.getPlayer()))).runTask();
	}

	public UserPerk refresh(WBUser user) {
		Player p = user.getBukkitEntity();
		boolean mayDoubleJump = false;
		UserPerk ownerPerk = null;
		for (UserPerk perk : user.perks().perks(DoubleJumpPerk.DOUBLE_JUMP)) {
			if (p.getGameMode() == GameMode.SURVIVAL) {
				if (user.woolCount() >= perk.perk().cost() && perk.cooldown() == 0) {
					mayDoubleJump = true;
					ownerPerk = perk;
					break;
				}
			} else {
				mayDoubleJump = true;
			}
		}
		if (ownerPerk != null) {
			EventMayDoubleJump event = new EventMayDoubleJump(ownerPerk, true);
			Bukkit.getPluginManager().callEvent(event);
			mayDoubleJump = event.mayDoubleJump();
		}
		if (mayDoubleJump != p.getAllowFlight()) {
			p.setAllowFlight(mayDoubleJump);
		}
		return ownerPerk;
	}
}
