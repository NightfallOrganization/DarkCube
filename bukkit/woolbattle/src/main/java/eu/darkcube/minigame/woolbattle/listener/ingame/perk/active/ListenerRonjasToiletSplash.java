/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.RonjasToiletFlushPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ListenerRonjasToiletSplash extends BasicPerkListener {

	private static final double RANGE = 4;

	public ListenerRonjasToiletSplash() {
		super(RonjasToiletFlushPerk.RONJAS_TOILET_FLUSH);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		Egg egg = perk.owner().getBukkitEntity().launchProjectile(Egg.class);
		egg.setMetadata("source", new FixedMetadataValue(WoolBattle.getInstance(), perk.owner()));
		egg.setMetadata("perk",
				new FixedMetadataValue(WoolBattle.getInstance(), perk.perk().perkName().getName()));
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
				WBUser.onlineUsers().stream()
						.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
						.map(WBUser::getBukkitEntity).filter(bukkitEntity ->
								bukkitEntity.getLocation().distance(egg.getLocation()) < RANGE)
						.forEach(t -> {
							Vector v = egg.getVelocity().multiply(1.3);
							v.setY(egg.getVelocity().getY()).normalize().multiply(3)
									.setY(v.getY() + 1.2);
							t.setVelocity(v);
						});
			} else {
				WBUser.onlineUsers().stream()
						.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
						.map(WBUser::getBukkitEntity)
						.filter(bukkitEntity -> bukkitEntity.getWorld().equals(egg.getWorld()))
						.filter(bukkitEntity ->
								bukkitEntity.getLocation().distance(egg.getLocation()) < RANGE + 1)
						.forEach(t -> {
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
		WBUser user = (WBUser) egg.getMetadata("source").get(0).value();
		WBUser target = WBUser.getUser(t);
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
		return egg.getMetadata("perk").get(0).asString()
				.equals(RonjasToiletFlushPerk.RONJAS_TOILET_FLUSH.getName());
	}
}
