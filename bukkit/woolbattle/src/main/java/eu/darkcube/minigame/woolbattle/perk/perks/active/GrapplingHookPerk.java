/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerGrapplingHook;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class GrapplingHookPerk extends Perk {
	public static final PerkName GRAPPLING_HOOK = new PerkName("GRAPPLING_HOOK");

	public GrapplingHookPerk() {
		super(ActivationType.ACTIVE, GRAPPLING_HOOK, 12, 12, Item.PERK_GRAPPLING_HOOK,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_GRAPPLING_HOOK_COOLDOWN));
		addListener(new ListenerGrapplingHook(this));
	}
}
