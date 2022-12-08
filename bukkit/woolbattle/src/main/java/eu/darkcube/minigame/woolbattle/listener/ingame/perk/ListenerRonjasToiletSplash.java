/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerRonjasToiletSplash extends BasicPerkListener {

	private static final double RANGE = 4;
	public static final Item RONJAS_TOILET = PerkType.RONJAS_TOILET_SPLASH.getItem();
	public static final Item RONJAS_TOILET_COOLDOWN =
			PerkType.RONJAS_TOILET_SPLASH.getCooldownItem();

	public ListenerRonjasToiletSplash() {
		super(PerkType.RONJAS_TOILET_SPLASH);
	}

	@EventHandler
	public void handle(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.EGG) {
			Egg egg = (Egg) e.getEntity();
			if (!(egg.getShooter() instanceof Player)) {
				return;
			}
			if (!egg.hasMetadata("perk")) {
				return;
			}
			if (!egg.getMetadata("perk").get(0).asString()
					.equals(PerkType.RONJAS_TOILET_SPLASH.getPerkName().getName())) {
				return;
			}
			ParticleEffect.DRIP_WATER.display(.3F, 1F, .3F, 1, 250, egg.getLocation(), 50);

			if (egg.getTicksLived() <= 3) {
				WoolBattle.getInstance().getUserWrapper().getUsers().stream()
						.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
						.filter(u -> u.getBukkitEntity().getLocation()
								.distance(egg.getLocation()) < RANGE)
						.map(User::getBukkitEntity).forEach(t -> {
							Vector v = egg.getVelocity().multiply(1.3);
							v.setY(egg.getVelocity().getY()).normalize().multiply(3)
									.setY(v.getY() + 1.2);
							t.setVelocity(v);
						});
			} else {
				WoolBattle.getInstance().getUserWrapper().getUsers().stream()
						.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
						.filter(u -> u.getBukkitEntity().getWorld().equals(egg.getWorld()))
						.filter(u -> u.getBukkitEntity().getLocation()
								.distance(egg.getLocation()) < RANGE + 1)
						.map(User::getBukkitEntity).forEach(t -> {
							double x = t.getLocation().getX() - egg.getLocation().getX();
							double y = t.getLocation().getY() - egg.getLocation().getY();
							double z = t.getLocation().getZ() - egg.getLocation().getZ();
							t.setVelocity(new Vector(x, Math.max(1, y), z).normalize().multiply(2));
						});
			}
		}
	}

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			Player p = e.getPlayer();
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			if (item == null || item.getType() == Material.AIR) {
				return;
			}
			String itemid = ItemManager.getItemId(item);
			if (itemid == null)
				return;
			if (!itemid.equals(RONJAS_TOILET_COOLDOWN.getItemId())
					&& !itemid.equals(RONJAS_TOILET.getItemId())) {
				return;
			}
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			e.setCancelled(true);
			p.launchProjectile(Egg.class);
		}
	}

	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		if (e.getEntityType() == EntityType.EGG) {
			Egg egg = (Egg) e.getEntity();
			if (!(egg.getShooter() instanceof Player)) {
				return;
			}
			Player p = (Player) egg.getShooter();
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			ItemStack item = p.getItemInHand();
			if (item == null)
				return;
			String itemid = ItemManager.getItemId(item);
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (RONJAS_TOILET_COOLDOWN.getItemId().equals(itemid)) {
				Ingame.playSoundNotEnoughWool(user);
				e.setCancelled(true);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			} else if (!RONJAS_TOILET.getItemId().equals(itemid)) {
				return;
			}
			if (!p.getInventory().contains(Material.WOOL,
					PerkType.RONJAS_TOILET_SPLASH.getCost())) {
				Ingame.playSoundNotEnoughWool(user);
				e.setCancelled(true);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			}
			ItemManager.removeItems(user, p.getInventory(),
					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColorByte()),
					PerkType.RONJAS_TOILET_SPLASH.getCost());

			egg.setMetadata("perk",
					new FixedMetadataValue(WoolBattle.getInstance(), perk.getPerkName().getName()));

			new Scheduler() {
				int cd = PerkType.RONJAS_TOILET_SPLASH.getCooldown() + 1;

				@Override
				public void run() {
					if (cd <= 1) {
						this.cancel();
						perk.setCooldown(0);
						return;
					}
					perk.setCooldown(--cd);
				}
			}.runTaskTimer(20);
		}
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getEntityType() != EntityType.PLAYER || e.getDamager().getType() != EntityType.EGG)
			return;
		Player t = (Player) e.getEntity();
		Egg egg = (Egg) e.getDamager();
		if (!(egg.getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) egg.getShooter();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		User target = WoolBattle.getInstance().getUserWrapper().getUser(t.getUniqueId());
		if (user.getTeam() != target.getTeam() || user.isTrollMode()) {
			target.setLastHit(user);
			target.setTicksAfterLastHit(0);
		}
	}
}
