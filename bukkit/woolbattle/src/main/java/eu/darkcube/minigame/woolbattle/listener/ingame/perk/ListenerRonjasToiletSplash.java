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

	public ListenerRonjasToiletSplash() {
		super(PerkType.RONJAS_TOILET_SPLASH);
	}

	@Override
	protected boolean activateRight(User user, Perk perk) {
		Egg egg = user.getBukkitEntity().launchProjectile(Egg.class);
		egg.setMetadata("source", new FixedMetadataValue(WoolBattle.getInstance(), user));
		egg.setMetadata("perk",
				new FixedMetadataValue(WoolBattle.getInstance(), perk.getPerkName().getName()));
		return true;
	}

	@EventHandler
	public void handle(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.EGG) {
			Egg egg = (Egg) e.getEntity();
			if (!isEggPerk(egg)) {
				return;
			}
			ParticleEffect.DRIP_WATER.display(.3F, 1F, .3F, 1, 250, egg.getLocation(), 50);

			if (egg.getTicksLived() <= 3) {
				WoolBattle.getInstance().getUserWrapper().getUsers().stream()
						.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
						.filter(u -> u.getBukkitEntity().getLocation().distance(egg.getLocation())
								< RANGE).map(User::getBukkitEntity).forEach(t -> {
							Vector v = egg.getVelocity().multiply(1.3);
							v.setY(egg.getVelocity().getY()).normalize().multiply(3).setY(v.getY() + 1.2);
							t.setVelocity(v);
						});
			} else {
				WoolBattle.getInstance().getUserWrapper().getUsers().stream()
						.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
						.filter(u -> u.getBukkitEntity().getWorld().equals(egg.getWorld()))
						.filter(u -> u.getBukkitEntity().getLocation().distance(egg.getLocation())
								< RANGE + 1).map(User::getBukkitEntity).forEach(t -> {
							double x = t.getLocation().getX() - egg.getLocation().getX();
							double y = t.getLocation().getY() - egg.getLocation().getY();
							double z = t.getLocation().getZ() - egg.getLocation().getZ();
							t.setVelocity(new Vector(x, Math.max(1, y), z).normalize().multiply(2));
						});
			}
		}
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getEntityType() != EntityType.PLAYER || e.getDamager().getType() != EntityType.EGG)
			return;
		Player t = (Player) e.getEntity();
		Egg egg = (Egg) e.getDamager();
		if (!isEggPerk(egg)) {
			return;
		}
		User user = (User) egg.getMetadata("source").get(0).value();
		User target = WoolBattle.getInstance().getUserWrapper().getUser(t.getUniqueId());
		if (user.getTeam() != target.getTeam() || user.isTrollMode()) {
			target.setLastHit(user);
			target.setTicksAfterLastHit(0);
		}
	}

	private boolean isEggPerk(Egg egg) {
		if (!egg.hasMetadata("source")) {
			return false;
		}
		if (!egg.hasMetadata("perk")) {
			return false;
		}
		if (!egg.getMetadata("perk").get(0).asString()
				.equals(PerkType.RONJAS_TOILET_SPLASH.getPerkName().getName())) {
			return false;
		}
		return true;
	}
}
