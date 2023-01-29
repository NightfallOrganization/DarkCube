/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.argument;

import java.util.Collection;
import java.util.Collections;

import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.system.stats.api.Arrays;

public class PerkTypeSelector {

	private final boolean all;
	private final PerkType type;

	public PerkTypeSelector(boolean all, PerkType type) {
		this.all = all;
		this.type = type;
	}

	public Collection<PerkType> select() {
		return all ? selectAll() : Collections.singleton(selectOne());
	}

	public Collection<PerkType> selectAll() {
		return Arrays.asList(PerkType.values());
	}

	public PerkType selectOne() {
		return type;
	}
}
