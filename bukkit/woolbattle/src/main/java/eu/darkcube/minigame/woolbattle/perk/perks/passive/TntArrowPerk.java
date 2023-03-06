/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;

public class TntArrowPerk extends Perk {
	public static final PerkName TNT_ARROW = new PerkName("TNT_ARROW");

	public TntArrowPerk() {
		super(ActivationType.PASSIVE, TNT_ARROW, new Cooldown(TimeUnit.TICKS, 7), false, 16,
				CostType.PER_SHOT, Item.PERK_TNT_ARROW,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_TNT_ARROW_COOLDOWN));
	}
}
