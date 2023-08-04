/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowArrowHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowShootArrowEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.FixedMetadataValue;

public class HookArrowPerk extends Perk {

	public static final PerkName HOOK_ARROW = new PerkName("HOOK_ARROW");

	public HookArrowPerk() {
		super(ActivationType.PASSIVE, HOOK_ARROW, new Cooldown(Unit.ACTIVATIONS, 3), false, 8,
				CostType.PER_ACTIVATION, Item.PERK_HOOK_ARROW,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_HOOK_ARROW_COOLDOWN));
		addListener(new HookArrowListener());
	}

	public class HookArrowListener implements Listener {
		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		public void handle(BowArrowHitPlayerEvent event) {
			if (event.arrow().hasMetadata("hookArrow")) {
				int removed = event.shooter().removeWool(cost());
				if (removed < cost()) {
					event.shooter().addWool(removed);
					return;
				}
				event.target().getBukkitEntity()
						.teleport(event.shooter().getBukkitEntity(), TeleportCause.PLUGIN);
			}
		}

		@EventHandler
		public void handle(BowShootArrowEvent event) {
			for (UserPerk perk : event.user().perks().perks(perkName())) {
				if (perk.cooldown() == 0) {
					perk.cooldown(cooldown().cooldown());
					event.arrow().setMetadata("hookArrow",
							new FixedMetadataValue(WoolBattle.instance(), perk));
					break;
				}
				perk.cooldown(perk.cooldown() - 1);
			}
		}
	}
}