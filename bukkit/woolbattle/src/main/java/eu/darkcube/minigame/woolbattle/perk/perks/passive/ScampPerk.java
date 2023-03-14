/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;

public class ScampPerk extends Perk {
	public static final PerkName SCAMP = new PerkName("SCAMP");

	public ScampPerk() {
		super(ActivationType.PASSIVE, SCAMP, 0, 0, null, null);
	}
}
