/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PerkSelector {

	private final boolean all;
	private final Perk perk;

	public PerkSelector(boolean all, Perk perk) {
		this.all = all;
		this.perk = perk;
	}

	public Collection<Perk> select() {
		return all ? selectAll() : Collections.singleton(selectOne());
	}

	public Collection<Perk> selectAll() {
		return new ArrayList<>(WoolBattleBukkit.instance().perkRegistry().perks().values());
	}

	public Perk selectOne() {
		return perk;
	}
}
