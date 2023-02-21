/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.ArrowBombPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ListenerArrowBomb extends BasicPerkListener {
	public ListenerArrowBomb() {
		super(ArrowBombPerk.ARROW_BOMB);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		Snowball snowball = perk.owner().getBukkitEntity().launchProjectile(Snowball.class);
		snowball.setVelocity(snowball.getVelocity().multiply(.7));
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
			int count = 20;
			float increase = 360F / count;
			Vector dir = new Vector(1, 1, 0).multiply(.2);

			Location l = new Location(null, 0, 0, 0);
			l.setDirection(dir);
			for (int i = 0; i < count; i++) {
				l.setYaw(l.getYaw() + increase);
				dir = l.getDirection();
				Arrow arrow = user.getBukkitEntity().getWorld()
						.spawnArrow(snowball.getLocation(), dir, .7F, 0);
				arrow.setShooter(user.getBukkitEntity());
				arrow.setKnockbackStrength(1);
				WoolBattle.getInstance().getIngame().arrows.put(arrow, user);
			}
		}
	}
}
