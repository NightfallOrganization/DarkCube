/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerBooster;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class BoosterPerk extends Perk {
	public static final PerkName BOOSTER = new PerkName("BOOSTER");

	public BoosterPerk() {
		super(ActivationType.ACTIVE, BOOSTER, 18, 12, Item.PERK_BOOSTER,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_BOOSTER_COOLDOWN));
		addListener(new ListenerBooster(this));
	}
}
