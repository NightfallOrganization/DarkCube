/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerRonjasToiletFlush;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class RonjasToiletFlushPerk extends Perk {
	public static final PerkName RONJAS_TOILET_FLUSH = new PerkName("RONJAS_TOILET_FLUSH");

	public RonjasToiletFlushPerk() {
		super(ActivationType.ACTIVE, RONJAS_TOILET_FLUSH, 13, 12, Item.PERK_RONJAS_TOILET_SPLASH,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_RONJAS_TOILET_SPLASH_COOLDOWN));
		addListener(new ListenerRonjasToiletFlush(this));
	}
}
