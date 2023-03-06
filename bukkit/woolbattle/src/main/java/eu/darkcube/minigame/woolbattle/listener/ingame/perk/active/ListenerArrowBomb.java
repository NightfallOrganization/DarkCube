/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.perks.active.ArrowBombPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Random;

public class ListenerArrowBomb extends BasicPerkListener {
	private final Random r = new Random();

	public ListenerArrowBomb(Perk perk) {
		super(perk);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		Snowball snowball = perk.owner().getBukkitEntity().launchProjectile(Snowball.class);
		snowball.setVelocity(snowball.getVelocity()); // de-sync fix
		snowball.setMetadata("perk",
				new FixedMetadataValue(WoolBattle.getInstance(), perk.perk().perkName()));
		snowball.setMetadata("user",
				new FixedMetadataValue(WoolBattle.getInstance(), perk.owner()));
		return true;
	}

	@EventHandler
	public void handle(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball))
			return;
		Snowball snowball = (Snowball) event.getEntity();
		if (!snowball.hasMetadata("perk"))
			return;
		if (snowball.getMetadata("perk").get(0).value().equals(ArrowBombPerk.ARROW_BOMB)) {
			WBUser user = (WBUser) snowball.getMetadata("user").get(0).value();
			int count = 30;
			Vector dir = new Vector(1, 0.3, 0);

			Location l = new Location(null, 0, 0, 0);
			l.setDirection(dir);
			for (int i = 0; i < count; i++) {
				float pitch = r.nextFloat() * 50F + 10;
				float yaw = r.nextInt(360 * 2) / 2F;
				if (r.nextFloat() < 0.1) {
					pitch = pitch - 10 + r.nextFloat() * 40F;
					l.setPitch(pitch);
				} else {
					l.setPitch(-pitch);
				}
				l.setYaw(yaw);
				dir = l.getDirection();
				Arrow arrow = user.getBukkitEntity().getWorld()
						.spawnArrow(snowball.getLocation(), dir, .9F, 0);
				arrow.setMetadata("noParticles",
						new FixedMetadataValue(WoolBattle.getInstance(), true));
				arrow.setShooter(user.getBukkitEntity());
				arrow.setKnockbackStrength(3);
			}
		}
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Snowball) {
			Snowball snowball = (Snowball) event.getDamager();
			if (!snowball.hasMetadata("perk"))
				return;
			if (snowball.getMetadata("perk").get(0).value().equals(ArrowBombPerk.ARROW_BOMB)) {
				event.setCancelled(true);
			}
		}
	}
}
