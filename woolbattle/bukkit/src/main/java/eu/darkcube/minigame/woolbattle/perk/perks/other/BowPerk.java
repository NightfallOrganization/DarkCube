/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.other;

import eu.darkcube.minigame.woolbattle.event.perk.other.BowShootArrowEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;

public class BowPerk extends Perk {
	public static final PerkName BOW = new PerkName("BOW");

	public BowPerk() {
		super(ActivationType.PRIMARY_WEAPON, BOW, 0, 1, Item.DEFAULT_BOW,
				(user, perk, id, perkSlot) -> new DefaultUserPerk(user, id, perkSlot, perk));
		addListener(new BowListener(this));
	}

	private static class BowListener extends BasicPerkListener {
		private Vector force;

		public BowListener(Perk perk) {
			super(perk);
		}

		@Override
		protected boolean activate(UserPerk perk) {
			Arrow arrow = perk.owner().getBukkitEntity().launchProjectile(Arrow.class, force);
			ArrowPerk.claimArrow(arrow, perk.owner(), 2, 1);
			arrow.setVelocity(arrow.getVelocity());
			perk.owner().getBukkitEntity().getWorld()
					.playSound(perk.owner().getBukkitEntity().getLocation(), Sound.SHOOT_ARROW, 1,
							1);
			BowShootArrowEvent event = new BowShootArrowEvent(perk.owner(), arrow);
			Bukkit.getPluginManager().callEvent(event);
			return true;
		}

		@Override
		protected boolean mayActivate() {
			return false;
		}

		@EventHandler
		public void handle(EntityShootBowEvent event) {
			if (event.getBow() == null || !event.getBow().hasItemMeta())
				return;
			if (!(event.getEntity() instanceof Player))
				return;
			WBUser user = WBUser.getUser((Player) event.getEntity());
			AtomicReference<UserPerk> refUserPerk = new AtomicReference<>();
			if (!checkUsable(user, event.getBow(), perk(), userPerk -> {
				refUserPerk.set(userPerk);
				event.setCancelled(true);
				event.getProjectile().remove();
			})) {
				return;
			}
			UserPerk userPerk = refUserPerk.get();
			force = event.getProjectile().getVelocity();
			if (!activate(userPerk)) {
				return;
			}
			activated(userPerk);
		}
	}
}
