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

public class MinigunPerk extends Perk {
	public static final PerkName MINIGUN = new PerkName("MINIGUN");

	public MinigunPerk() {
		super(ActivationType.ACTIVE, MINIGUN, 10, 1, CostType.PER_SHOT, Item.PERK_MINIGUN,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_MINIGUN_COOLDOWN));
	}
}
