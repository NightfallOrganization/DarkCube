/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerWoolBomb;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class WoolBombPerk extends Perk {
	public static final PerkName WOOL_BOMB = new PerkName("WOOL_BOMB");

	public WoolBombPerk() {
		super(ActivationType.ACTIVE, WOOL_BOMB, 14, 8, Item.PERK_WOOL_BOMB,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_WOOL_BOMB_COOLDOWN));
		addListener(new ListenerWoolBomb(this));
	}
}
