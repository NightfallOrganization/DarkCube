/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;

public class ProtectiveShieldPerk extends Perk {
	public static final PerkName PROTECTIVE_SHIELD = new PerkName("PROTECTIVE_SHIELD");

	public ProtectiveShieldPerk() {
		super(ActivationType.ACTIVE, PROTECTIVE_SHIELD, 15, 5, Item.PERK_PROTECTIVE_SHIELD,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_PROTECTIVE_SHIELD_COOLDOWN));
		addListener(new ListenerProtectiveShield(this));
	}

	public static class ListenerProtectiveShield extends BasicPerkListener {
		public ListenerProtectiveShield(Perk perk) {
			super(perk);
		}

		@Override
		protected boolean activateRight(UserPerk perk) {
			perk.owner().projectileImmunityTicks(TimeUnit.SECOND.itoTicks(5));
			return true;
		}
	}
}
