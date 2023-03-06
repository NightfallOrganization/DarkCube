/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive.ListenerLongJump;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class LongJumpPerk extends Perk {
	public static final PerkName LONG_JUMP = new PerkName("LONG_JUMP");

	public LongJumpPerk() {
		super(ActivationType.PASSIVE, LONG_JUMP, 0, 0, Item.PERK_LONGJUMP,
				(user, perk, id, perkSlot) -> new DefaultUserPerk(user, id, perkSlot, perk));
		addListener(new ListenerLongJump(this));
	}
}
