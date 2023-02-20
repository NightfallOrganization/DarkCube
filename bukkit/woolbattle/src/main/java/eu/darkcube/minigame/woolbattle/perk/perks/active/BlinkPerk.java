/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class BlinkPerk extends Perk {
	public static final PerkName BLINK = new PerkName("BLINK");

	public BlinkPerk() {
		super(ActivationType.ACTIVE, BLINK, 15, 12, Item.PERK_BLINK,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_BLINK_COOLDOWN));
	}
}
