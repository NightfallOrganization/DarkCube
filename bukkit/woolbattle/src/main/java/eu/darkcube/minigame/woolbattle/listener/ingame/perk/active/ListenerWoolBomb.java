/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.WoolBombPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ListenerWoolBomb extends BasicPerkListener {

	public ListenerWoolBomb() {
		super(WoolBombPerk.WOOL_BOMB);
	}

	@Override
	protected boolean activate(UserPerk perk) {
		Player p = perk.owner().getBukkitEntity();
		Snowball bomb = p.launchProjectile(Snowball.class);
		bomb.setMetadata("source", new FixedMetadataValue(WoolBattle.getInstance(), perk.owner()));
		bomb.setMetadata("perk",
				new FixedMetadataValue(WoolBattle.getInstance(), perk.perk().perkName().getName()));
		return true;
	}

	@EventHandler
	public void handle(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.SNOWBALL) {
			Snowball bomb = (Snowball) e.getEntity();
			if (!bomb.hasMetadata("source")) {
				return;
			}
			if (!bomb.hasMetadata("perk")) {
				return;
			}
			if (!bomb.getMetadata("perk").get(0).asString()
					.equals(WoolBombPerk.WOOL_BOMB.getName())) {
				return;
			}
			WBUser user = (WBUser) bomb.getMetadata("source").get(0).value();
			TNTPrimed tnt = bomb.getWorld().spawn(bomb.getLocation(), TNTPrimed.class);

			tnt.setMetadata("boost", new FixedMetadataValue(WoolBattle.getInstance(), 3));
			tnt.setMetadata("source", new FixedMetadataValue(WoolBattle.getInstance(), user));
			tnt.setFuseTicks(10);
			tnt.setYield(4f);
		}
	}

}
