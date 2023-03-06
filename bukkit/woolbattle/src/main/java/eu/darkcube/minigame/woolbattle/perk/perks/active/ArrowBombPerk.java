/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerArrowBomb;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class ArrowBombPerk extends Perk {
	public static final PerkName ARROW_BOMB = new PerkName("ARROW_BOMB");

	public ArrowBombPerk() {
		super(ActivationType.ACTIVE, ARROW_BOMB, 9, 7, Item.PERK_ARROW_BOMB,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_ARROW_BOMB_COOLDOWN));
		addListener(new ListenerArrowBomb(this));
	}
}
