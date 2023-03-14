/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.perk.other.PlayerHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class ListenerEntityDamageByEntity extends Listener<EntityDamageByEntityEvent> {

	@Override
	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			e.setDamage(0);
			WoolBattle main = WoolBattle.instance();
			WBUser target = WBUser.getUser((Player) e.getEntity());
			Ingame ingame = main.getIngame();
			if (target.hasSpawnProtection() || ingame.isGlobalSpawnProtection) {
				e.setCancelled(true);
				return;
			}
			if (e.getDamager() instanceof Player) {
				WBUser user = WBUser.getUser((Player) e.getDamager());
				if (!ingame.canAttack(user, target)) {
					e.setCancelled(true);
				} else {
					PlayerHitPlayerEvent event = new PlayerHitPlayerEvent(user, target);
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						e.setCancelled(true);
					} else if (!ingame.attack(user, target)) {
						e.setCancelled(true);
					}
				}
			} else if (e.getDamager() instanceof Snowball) {
				Snowball ball = (Snowball) e.getDamager();
				if (ball.getShooter() instanceof Player) {
					Player p = (Player) ball.getShooter();
					WBUser user = WBUser.getUser(p);

					if (ball.hasMetadata("type") && ball.getMetadata("type").get(0).asString()
							.equals("minigun")) {
						if (target.projectileImmunityTicks() > 0) {
							e.setCancelled(true);
							return;
						}
						if (!WoolBattle.instance().getIngame().attack(user, target)) {
							e.setCancelled(true);
							return;
						}
						e.setCancelled(true);
						target.getBukkitEntity().damage(0);

						target.getBukkitEntity().setVelocity(ball.getVelocity().setY(0).normalize()
								.multiply(.47 + new Random().nextDouble() / 70 + 1.1)
								.setY(.400023));
					}
				}
			}
		} else {
			e.setCancelled(true);
		}
	}
}
