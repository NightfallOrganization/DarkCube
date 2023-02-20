/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.other;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class BowPerk extends Perk {
	public static final PerkName BOW = new PerkName("BOW");

	public BowPerk() {
		super(ActivationType.PRIMARY_WEAPON, BOW, 0, 0, Item.DEFAULT_BOW,
				(user, perk, id, perkSlot) -> new DefaultUserPerk(user, id, perkSlot, perk));
	}
}
