/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class RocketJumpPerk extends Perk {
	public static final PerkName ROCKET_JUMP = new PerkName("ROCKET_JUMP");

	public RocketJumpPerk() {
		super(ActivationType.PASSIVE, ROCKET_JUMP, 0, 0, Item.PERK_ROCKETJUMP,
				(user, perk, id, perkSlot) -> new DefaultUserPerk(user, id, perkSlot, perk));
	}
}