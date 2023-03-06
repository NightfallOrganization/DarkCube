/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ListenerEntityDamageByEntity extends Listener<EntityDamageByEntityEvent> {

	@Override
	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			e.setDamage(0);
			WoolBattle main = WoolBattle.getInstance();
			WBUser target = WBUser.getUser((Player) e.getEntity());
			Ingame ingame = main.getIngame();
			if (target.hasSpawnProtection() || ingame.isGlobalSpawnProtection) {
				e.setCancelled(true);
				return;
			}
			if (e.getDamager() instanceof Player) {
				WBUser user = WBUser.getUser((Player) e.getDamager());
				if (!user.isTrollMode()) {
					if (e.isCancelled())
						return;
					if (user.getTeam().getType() == TeamType.SPECTATOR) {
						e.setCancelled(true);
						return;
					}
				}
				if (target.getTeam().equals(user.getTeam()) && !user.isTrollMode()) {
					e.setCancelled(true);
					return;
				}
				if (target.hasSpawnProtection() && !user.isTrollMode()) {
					target.setSpawnProtectionTicks(0);
				}
				ingame.attack(user, target);
			} else if (e.getDamager() instanceof EnderPearl) {
				EnderPearl pearl = (EnderPearl) e.getDamager();
				if (pearl.getShooter() instanceof Player) {
					Player p = (Player) pearl.getShooter();
					WBUser user = WBUser.getUser(p);
					if (target.projectileImmunityTicks() == 0) {
						WoolBattle.getInstance().getIngame().attack(user, target);
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
						if (!WoolBattle.getInstance().getIngame().attack(user, target)) {
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

			} else if (e.getDamager() instanceof Arrow) {

				Arrow arrow = (Arrow) e.getDamager();
				if (arrow.getShooter() instanceof Player) {
					Player p = (Player) arrow.getShooter();
					WBUser user = WBUser.getUser(p);

					e.setCancelled(true);
					if (target.getTicksAfterLastHit() >= 10) {
						if (target.projectileImmunityTicks() > 0) {
							arrow.remove();
							return;
						}

						if (!WoolBattle.getInstance().getIngame().attack(user, target)) {
							arrow.remove();
							return;
						}
						target.getBukkitEntity().damage(0);
						user.getBukkitEntity().playSound(user.getBukkitEntity().getLocation(),
								Sound.SUCCESSFUL_HIT, 1, 0);
						target.getBukkitEntity().getWorld()
								.playSound(target.getBukkitEntity().getLocation(), Sound.ARROW_HIT,
										1, 1);

						new Scheduler() {
							Location loc = target.getBukkitEntity().getLocation();

							@SuppressWarnings("deprecation")
							private Set<Block> execute(double width, double hstart, double height) {
								width = Math.abs(width);
								width--;
								width /= 2;
								Set<Block> res = new HashSet<>();
								for (double xoff = -width; xoff < width + 1; xoff++) {
									for (double yoff = hstart; yoff < hstart + height; yoff++) {
										for (double zoff = -width; zoff < width + 1; zoff++) {
											Location l = this.loc.clone().add(xoff, yoff, zoff);
											Block b = l.getBlock();
											if (b.getType() == Material.WOOL) {
												int dmg = ingame.getBlockDamage(b);
												if (dmg >= 2) {
													Random r = new Random();
													double x = r.nextBoolean()
															? r.nextDouble() / 3
															: -r.nextDouble() / 3, y =
															r.nextDouble() / 2, z = r.nextBoolean()
															? r.nextDouble() / 3
															: -r.nextDouble() / 3;
													EntityFallingBlock block =
															new EntityFallingBlock(
																	((CraftWorld) b.getWorld()).getHandle(),
																	b.getLocation().getBlockX()
																			+ .5,
																	b.getLocation().getBlockY()
																			+ .5,
																	b.getLocation().getBlockZ()
																			+ .5,
																	Blocks.WOOL.fromLegacyData(
																			b.getData()));
													ingame.setBlockDamage(b, dmg + 1);
													block.k = false;
													block.ticksLived = 1;
													block.dropItem = false;
													block.motX = x;
													block.motY = y;
													block.motZ = z;
													block.velocityChanged = true;
													block.world.addEntity(block);
												} else {
													ingame.setBlockDamage(b, dmg + 1);
													res.add(b);
												}
											}
										}
									}
								}
								return res;
							}

							@Override
							public void run() {
								double width = 2;
								//								double capsuleWidth = 3;
								double hstart = -1;
								double height = 4;

								Set<Block> s1 = this.execute(width, hstart, height);
								//								this.execute(capsuleWidth, hstart, height, b -> {
								//									MetadataValue v = Ingame.getMetaData(b, "capsule");
								//									boolean isCapsule = v != null && v.asBoolean();
								//									return !s1.contains(b) && isCapsule;
								//								});

							}
						}.runTaskLater(3);
						target.getBukkitEntity().setVelocity(arrow.getVelocity().setY(0).normalize()
								.multiply(.47 + new Random().nextDouble() / 70
										+ arrow.getKnockbackStrength() / 1.42).setY(.400023));
						arrow.remove();
					} else {
						arrow.remove();
					}
				}
			}
		} else {
			e.setCancelled(true);
		}
	}
}
