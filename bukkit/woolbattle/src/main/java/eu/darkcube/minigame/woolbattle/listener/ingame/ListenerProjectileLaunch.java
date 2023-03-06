/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.ArrowRainPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.FastArrowPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.TntArrowPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ListenerProjectileLaunch extends Listener<ProjectileLaunchEvent> {

	@Override
	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			WBUser user = WBUser.getUser(p);
			if (e.getEntityType() == EntityType.ARROW) {
				Arrow arrow = (Arrow) e.getEntity();
				if (arrow.hasMetadata("ignoreArrowShoot"))
					return;

				if (!p.getInventory().contains(Material.WOOL, 1)) {
					WoolBattle.getInstance().getIngame().playSoundNotEnoughWool(user);
					arrow.remove();
					e.setCancelled(true);
					return;
				}

				user.removeWool(1);
				arrow.setVelocity(arrow.getVelocity()); // This is the fix for a server client
				// de-sync problem. We just need to send the velocity packet for the arrow again.
				// Might change this in the future to use actual packets or to modify the nms
				// class, but like what's the point lol?
				if (user.perks().count(FastArrowPerk.FAST_ARROW) > 0) {
					Vector vec = arrow.getVelocity().multiply(1.7);

					double[] ds = new double[] {vec.getX(), vec.getY(), vec.getZ()};

					double div = 1;
					for (double d : ds) {
						if (d < -4 || d > 4) {
							d = Math.abs(d);
							double m = d / 4.0;
							if (div < m) {
								div = m;
							}
						}
					}
					vec = vec.divide(new Vector(div, div, div)).multiply(0.999);
					if (vec.length() > 4) {
						vec = vec.normalize().multiply(4);
					}

					arrow.setVelocity(vec);
				}
				for (UserPerk perk : user.perks().perks(ActivationType.PASSIVE)) {
					if (perk.perk().perkName().equals(ArrowRainPerk.ARROW_RAIN)) {
						int arrowCount = 6;
						arrowCount = (arrowCount / 2) * 2;

						if (perk.cooldown() > 0) {
							perk.cooldown(perk.cooldown() - 1);
							return;
						}
						if (!p.getInventory().contains(Material.WOOL, perk.perk().cost())) {
							new Scheduler() {
								@Override
								public void run() {
									perk.currentPerkItem().setItem();
								}
							}.runTask();
							return;
						}

						int cost = perk.perk().cost() - arrowCount;
						ItemStack wool = new ItemStack(Material.WOOL, 1,
								user.getTeam().getType().getWoolColorByte());

						if (cost < 0) {
							wool.setAmount(-cost);
							p.getInventory().addItem(wool);
						} else {
							ItemManager.removeItems(user, p.getInventory(), wool, cost);
						}
						perk.cooldown(perk.perk().cooldown().ticks() + arrowCount);

						this.shootArrows(user, 20F / arrowCount, arrowCount / 2, arrow);
						this.shootArrows(user, -20F / arrowCount, arrowCount / 2, arrow);
					} else if (perk.perk().perkName().equals(TntArrowPerk.TNT_ARROW)) {
						if (perk.cooldown() > 0) {
							perk.cooldown(perk.cooldown() - 1);
							return;
						}
						perk.cooldown(perk.perk().cooldown().ticks());
						new Scheduler() {
							@Override
							public void run() {
								if (arrow.isDead() || !arrow.isValid()) {
									this.cancel();
									if (!p.getInventory()
											.contains(Material.WOOL, perk.perk().cost())) {
										perk.currentPerkItem().setItem();
										return;
									}

									int cost = perk.perk().cost();
									ItemStack wool = user.getSingleWoolItem();
									if (cost < 0) {
										wool.setAmount(-cost);
										p.getInventory().addItem(wool);
									} else {
										ItemManager.removeItems(user, p.getInventory(), wool, cost);
									}
									TNTPrimed tnt = arrow.getWorld()
											.spawn(arrow.getLocation(), TNTPrimed.class);

									tnt.setMetadata("boost",
											new FixedMetadataValue(WoolBattle.getInstance(), 2));
									tnt.setMetadata("source",
											new FixedMetadataValue(WoolBattle.getInstance(), user));
									tnt.setIsIncendiary(false);
									tnt.setFuseTicks(2);
								}
							}
						}.runTaskTimer(1);
					}
				}
			}
		}
	}

	private void shootArrows(WBUser user, float angle, int count, Arrow original) {
		Player p = user.getBukkitEntity();
		for (int i = 0; i < count; i++) {
			Arrow arrow = p.launchProjectile(Arrow.class);
			arrow.setKnockbackStrength(2);
			Vector vec = original.getVelocity();
			Location l = new Location(null, 0, 0, 0);
			l.setDirection(vec);
			l.setYaw(l.getYaw() + (i + 1) * angle);
			vec = l.getDirection();
			vec.multiply(original.getVelocity().length());
			arrow.setVelocity(vec);
		}
	}

}
