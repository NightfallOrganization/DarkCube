/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.other;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowArrowHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;

public class ArrowPerk extends Perk {
	public static final PerkName ARROW = new PerkName("ARROW");

	public ArrowPerk() {
		super(ActivationType.ARROW, ARROW, 0, 0, Item.DEFAULT_ARROW,
				(user, perk, id, perkSlot) -> new DefaultUserPerk(user, id, perkSlot, perk));
		addListener(new ArrowPerkListener());
	}

	public static void claimArrow(Arrow arrow, WBUser user, float strength) {
		arrow.setMetadata("user", new FixedMetadataValue(WoolBattle.instance(), user));
		arrow.setMetadata("perk", new FixedMetadataValue(WoolBattle.instance(), ArrowPerk.ARROW));
		arrow.setMetadata("strength", new FixedMetadataValue(WoolBattle.instance(), strength));
	}

	private class ArrowPerkListener implements Listener {
		@EventHandler
		public void handle(EntityDamageByEntityEvent event) {
			if (!(event.getEntity() instanceof Player))
				return;
			if (!(event.getDamager() instanceof Arrow))
				return;
			WBUser user = WBUser.getUser((Player) event.getEntity());
			Arrow arrow = (Arrow) event.getDamager();
			if (!arrow.hasMetadata("perk"))
				return;
			if (!arrow.getMetadata("perk").get(0).value().equals(perkName()))
				return;
			WBUser shooter = (WBUser) arrow.getMetadata("user").get(0).value();
			float strength = arrow.getMetadata("strength").get(0).asFloat();
			event.setCancelled(true);
			if (user.getTicksAfterLastHit() < 10)
				return;
			if (user.projectileImmunityTicks() > 0) {
				arrow.remove();
				return;
			}
			if (!WoolBattle.instance().getIngame().canAttack(shooter, user)) {
				arrow.remove();
				return;
			}
			BowArrowHitPlayerEvent hitEvent = new BowArrowHitPlayerEvent(arrow, shooter, user);
			Bukkit.getPluginManager().callEvent(hitEvent);
			if (hitEvent.isCancelled()) {
				arrow.remove();
				return;
			}
			if (!WoolBattle.instance().getIngame().attack(shooter, user)) {
				arrow.remove();
				return;
			}
			user.getBukkitEntity().damage(0);
			shooter.getBukkitEntity()
					.playSound(shooter.getBukkitEntity().getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
			user.getBukkitEntity().getWorld().playSound(arrow.getLocation(), Sound.ARROW_HIT, 1, 1);
			user.getBukkitEntity().setVelocity(arrow.getVelocity().setY(0).normalize()
					.multiply(.47 + new Random().nextDouble() / 70 + strength / 1.42)
					.setY(.400023));

			new Scheduler() {
				@Override
				public void run() {
					Location loc = user.getBukkitEntity().getLocation();
					execute(WoolBattle.instance().getIngame(), loc);
				}

				private void /*Set<Block*/ execute(Ingame ingame, Location loc) {
					double width = 0.7;
					//					Set<Block> res = new HashSet<>();
					for (double xoff = -width; xoff < width + 1; xoff++) {
						for (double yoff = -1; yoff < 3; yoff++) {
							for (double zoff = -width; zoff < width + 1; zoff++) {
								Location l = loc.clone().add(xoff, yoff, zoff);
								Block b = l.getBlock();
								if (b.getType() == Material.WOOL) {
									int dmg = ingame.getBlockDamage(b);
									//									if (dmg >= 2) {
									//										Random r = new Random();
									//										double x = r.nextBoolean()
									//												? r.nextDouble() / 3
									//												: -r.nextDouble() / 3, y = r.nextDouble() / 2, z =
									//												r.nextBoolean()
									//														? r.nextDouble() / 3
									//														: -r.nextDouble() / 3;
									//										EntityFallingBlock block = new EntityFallingBlock(
									//												((CraftWorld) b.getWorld()).getHandle(),
									//												b.getLocation().getBlockX() + .5,
									//												b.getLocation().getBlockY() + .5,
									//												b.getLocation().getBlockZ() + .5,
									//												Blocks.WOOL.fromLegacyData(b.getData()));
									ingame.setBlockDamage(b, dmg + 1);
									//										block.k = false;
									//										block.ticksLived = 1;
									//										block.dropItem = false;
									//										block.motX = x;
									//										block.motY = y;
									//										block.motZ = z;
									//										block.velocityChanged = true;
									//										block.world.addEntity(block);
									//									} else {
									//										ingame.setBlockDamage(b, dmg + 1);
									//										res.add(b);
									//									}
								}
							}
						}
					}
					//					return res;
				}
			}.runTaskLater(3);

		}
	}
}
