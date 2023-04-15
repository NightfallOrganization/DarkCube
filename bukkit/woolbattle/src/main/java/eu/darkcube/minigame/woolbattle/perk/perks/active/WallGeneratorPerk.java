/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerWallGenerator;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class WallGeneratorPerk extends Perk {
	public static final PerkName WALL_GENERATOR = new PerkName("WALL_GENERATOR");

	public WallGeneratorPerk() {
		super(ActivationType.ACTIVE, WALL_GENERATOR, 9, 1, CostType.PER_BLOCK,
				Item.PERK_WALL_GENERATOR,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_WALL_GENERATOR_COOLDOWN));
		addListener(new ListenerWallGenerator(this));
	}
}
