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

public class ArrowPerk extends Perk {
	public static final PerkName ARROW = new PerkName("ARROW");

	public ArrowPerk() {
		super(ActivationType.ARROW, ARROW, 0, 1, Item.DEFAULT_ARROW,
				(user, perk, id, perkSlot) -> new DefaultUserPerk(user, id, perkSlot, perk));
	}
}
