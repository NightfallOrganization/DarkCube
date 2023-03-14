/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.perks.active.SwitcherPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ListenerSwitcher extends BasicPerkListener {

	public ListenerSwitcher(Perk perk) {
		super(perk);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		Snowball ball = perk.owner().getBukkitEntity().launchProjectile(Snowball.class);
		ball.setMetadata("perk",
				new FixedMetadataValue(WoolBattle.instance(), perk.perk().perkName().getName()));
		return true;
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		if (e.getDamager().getType() != EntityType.SNOWBALL) {
			return;
		}
		Snowball snowball = (Snowball) e.getDamager();
		if (!(snowball.getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) snowball.getShooter();
		Player hit = (Player) e.getEntity();
		if (snowball.getMetadata("perk").size() != 0 && snowball.getMetadata("perk").get(0)
				.asString().equals(SwitcherPerk.SWITCHER.getName())) {
			e.setCancelled(true);
			WBUser user = WBUser.getUser(hit);
			if (user.projectileImmunityTicks() > 0)
				return;
			if (user.getTicksAfterLastHit() < TimeUnit.SECOND.toTicks(30))
				WoolBattle.instance().getIngame().attack(WBUser.getUser(p), user);
			Location loc = p.getLocation();
			p.teleport(hit);
			hit.teleport(loc);

			p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1.8F);
			hit.playSound(hit.getLocation(), Sound.ITEM_PICKUP, 1, 1.8F);
		}
	}
}
