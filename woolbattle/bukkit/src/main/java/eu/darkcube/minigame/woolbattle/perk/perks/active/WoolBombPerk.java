/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class WoolBombPerk extends Perk {
	public static final PerkName WOOL_BOMB = new PerkName("WOOL_BOMB");

	public WoolBombPerk() {
		super(ActivationType.ACTIVE, WOOL_BOMB, 14, 8, Item.PERK_WOOL_BOMB,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_WOOL_BOMB_COOLDOWN));
		addListener(new ListenerWoolBomb(this));
	}

	public static class ListenerWoolBomb extends BasicPerkListener {

		public ListenerWoolBomb(Perk perk) {
			super(perk);
		}

		@Override
		protected boolean activate(UserPerk perk) {
			Player p = perk.owner().getBukkitEntity();
			Snowball bomb = p.launchProjectile(Snowball.class);
			bomb.setMetadata("source", new FixedMetadataValue(WoolBattleBukkit.instance(),
					perk.owner()));
			bomb.setMetadata("perk", new FixedMetadataValue(WoolBattleBukkit.instance(),
					perk.perk().perkName().getName()));
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

				tnt.setMetadata("boost", new FixedMetadataValue(WoolBattleBukkit.instance(), 3));
				tnt.setMetadata("source", new FixedMetadataValue(WoolBattleBukkit.instance(), user));
				tnt.setFuseTicks(10);
				tnt.setYield(4f);
			}
		}

	}
}
