/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerSwitcher;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class SwitcherPerk extends Perk {
	public static final PerkName SWITCHER = new PerkName("SWITCHER");

	public SwitcherPerk() {
		super(ActivationType.ACTIVE, SWITCHER, 7, 8, Item.PERK_SWITCHER,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_SWITCHER_COOLDOWN));
		addListener(new ListenerSwitcher(this));
	}
}
