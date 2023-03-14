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
import eu.darkcube.minigame.woolbattle.perk.perks.active.FreezerPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ListenerFreezer extends BasicPerkListener {
	public ListenerFreezer(Perk perk) {
		super(perk);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		Snowball snowball = perk.owner().getBukkitEntity().launchProjectile(Snowball.class);
		snowball.setMetadata("perk",
				new FixedMetadataValue(WoolBattle.instance(), perk.perk().perkName().toString()));
		return true;
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (event.getDamager().getType() != EntityType.SNOWBALL) {
			return;
		}
		Snowball snowball = (Snowball) event.getDamager();
		if (!(snowball.getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) snowball.getShooter();
		Player hit = (Player) event.getEntity();
		if (snowball.getMetadata("perk").size() != 0 && snowball.getMetadata("perk").get(0)
				.asString().equals(FreezerPerk.FREEZER.getName())) {
			event.setCancelled(true);
			WBUser user = WBUser.getUser(hit);
			if (user.projectileImmunityTicks() > 0)
				return;
			if (user.getTeam().getType() != WBUser.getUser(p).getTeam().getType()) {
				user.getBukkitEntity().addPotionEffect(
						new PotionEffect(PotionEffectType.SLOW, TimeUnit.SECOND.itoTicks(3), 6,
								true, false), true);
			}
		}
	}
}
