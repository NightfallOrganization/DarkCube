/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerTNTEntityDamageByEntity extends Listener<EntityDamageByEntityEvent> {

	@SuppressWarnings("deprecation")
	@Override
	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getEntityType() != EntityType.PLAYER) {
			return;
		}
		Player p = (Player) e.getEntity();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (user.getTeam().getType() == TeamType.SPECTATOR) {
			e.setCancelled(true);
			return;
		}
		if (e.getDamager().getType() == EntityType.PRIMED_TNT) {
			TNTPrimed tnt = (TNTPrimed) e.getDamager();
			if (tnt.hasMetadata("source")) {
				User source = (User) tnt.getMetadata("source").get(0).value();
				if (tnt.getLocation().distance(p.getLocation()) > tnt.getYield()) {
					e.setCancelled(true);
					return;
				}
				Player a = source.getBukkitEntity();
				Location loc = p.getLocation().add(0, 0.5, 0);
				User attacker = WoolBattle.getInstance().getUserWrapper().getUser(a.getUniqueId());
				e.setCancelled(true);
				double x = loc.getX() - tnt.getLocation().getX();
				double y = loc.getY() - tnt.getLocation().getY();
				y = y < 0.7 ? 0.7 : y;
				double z = loc.getZ() - tnt.getLocation().getZ();
				Vector direction = new Vector(x, y, z).normalize();
				double strength = 0;
				strength += tnt.getMetadata("boost").get(0).asDouble();

				double t = (tnt.getYield() - tnt.getLocation().distance(loc)) / (tnt.getYield() * 2) + 0.5;
				strength *= t;
				strength *= 1.2;
				if (!p.isOnGround()) {
					strength *= 1.2;
				}

				double strengthX = strength;
				double strengthY = strength;
				double strengthZ = strength;

				if (a.equals(p)) {
					if (p.getLocation().distance(tnt.getLocation()) < 1.3) {
						strengthX *= 0.2;
						strengthZ *= 0.2;
					}
				}

				Vector velocity = direction.clone();
				velocity.setX(velocity.getX() * strengthX);
				velocity.setY(1 + (velocity.getY() * strengthY / 5));
				velocity.setZ(velocity.getZ() * strengthZ);
				p.setVelocity(velocity);
				WoolBattle.getInstance().getIngame().attack(attacker, user);
			}
		} else if (e.getDamager().getType() == EntityType.SNOWBALL) {
			Snowball bomb = (Snowball) e.getDamager();
			if (bomb.getMetadata("perk").size() != 0
					&& bomb.getMetadata("perk").get(0).asString().equals(PerkType.WOOL_BOMB.getPerkName().getName())) {
				e.setCancelled(true);
			}
		}
	}

//
//	private static double calc(double dist, double rad) {
////		return rad - dist <= 0 ? 0 : dist == 0 ? 1 : Math.pow(Math.pow(1 - dist / 1.3 / rad, 3), .6);
//		return rad - dist <= 0 ? 0 : dist == 0 ? 1 : Math.pow(1 - dist / 1.4 / rad, .7);
//	}
}
