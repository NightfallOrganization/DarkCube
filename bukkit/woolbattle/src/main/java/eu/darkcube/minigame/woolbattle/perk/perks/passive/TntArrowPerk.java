/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowShootArrowEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

public class TntArrowPerk extends Perk {
	public static final PerkName TNT_ARROW = new PerkName("TNT_ARROW");

	public TntArrowPerk() {
		super(ActivationType.PASSIVE, TNT_ARROW, new Cooldown(Unit.ACTIVATIONS, 7), false, 16,
				CostType.PER_SHOT, Item.PERK_TNT_ARROW,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_TNT_ARROW_COOLDOWN));
		addListener(new TntArrowListener());
	}

	private static class TntArrowListener implements Listener {
		@EventHandler
		public void handle(BowShootArrowEvent event) {
			Arrow arrow = event.arrow();
			for (UserPerk perk : event.user().perks().perks(TNT_ARROW)) {
				if (perk.cooldown() > 0) {
					perk.cooldown(perk.cooldown() - 1);
					continue;
				}
				perk.cooldown(perk.perk().cooldown().cooldown());
				new Scheduler() {
					@Override
					public void run() {
						if (arrow.isDead() || !arrow.isValid()) {
							this.cancel();
							if (event.user().removeWool(perk.perk().cost()) != perk.perk().cost()) {
								perk.currentPerkItem().setItem();
								return;
							}

							TNTPrimed tnt =
									arrow.getWorld().spawn(arrow.getLocation(), TNTPrimed.class);

							tnt.setMetadata("boost",
									new FixedMetadataValue(WoolBattle.instance(), 2));
							tnt.setMetadata("source",
									new FixedMetadataValue(WoolBattle.instance(), event.user()));
							tnt.setIsIncendiary(false);
							tnt.setFuseTicks(2);
						}
					}
				}.runTaskTimer(1);
			}
		}
	}
}
