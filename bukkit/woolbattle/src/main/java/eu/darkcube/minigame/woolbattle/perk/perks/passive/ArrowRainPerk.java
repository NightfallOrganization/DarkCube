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

public class ArrowRainPerk extends Perk {
	public static final PerkName ARROW_RAIN = new PerkName("ARROW_RAIN");

	public ArrowRainPerk() {
		super(ActivationType.PASSIVE, ARROW_RAIN, 6, 0, CostType.PER_ACTIVATION,
				Item.PERK_ARROW_RAIN,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_ARROW_RAIN_COOLDOWN));
	}
}
