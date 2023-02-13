/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.user;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;

public class CooldownUserPerk extends DefaultUserPerk {
	private final Item cooldownItem;

	public CooldownUserPerk(WBUser owner, int id, int perkSlot, Perk perk, Item cooldownItem) {
		super(owner, id, perkSlot, perk);
		this.cooldownItem = cooldownItem;
	}

	protected boolean useCooldownItem() {
		return this.cooldown() > 0;
	}

	@Override
	protected Item currentItem() {
		return useCooldownItem() ? super.currentItem() : cooldownItem;
	}
}
