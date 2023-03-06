/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerGrandpasClock;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class GrandpasClockPerk extends Perk {
	public static final PerkName GRANDPAS_CLOCK = new PerkName("GRANDPAS_CLOCK");

	public GrandpasClockPerk() {
		super(ActivationType.ACTIVE, GRANDPAS_CLOCK, 16, 18, Item.PERK_GRANDPAS_CLOCK,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_GRANDPAS_CLOCK_COOLDOWN));
		addListener(new ListenerGrandpasClock(this));
	}
}
