/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.other;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class EnderPearlPerk extends Perk {
	public static final PerkName ENDERPEARL = new PerkName("ENDERPEARL");

	public EnderPearlPerk() {
		super(ActivationType.MISC, ENDERPEARL, 5, 8, Item.DEFAULT_PEARL,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.DEFAULT_PEARL_COOLDOWN));
	}
}
