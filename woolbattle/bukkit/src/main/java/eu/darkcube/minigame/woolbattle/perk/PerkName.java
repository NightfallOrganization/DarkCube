/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk;

import java.util.Objects;

public class PerkName {

	private final String name;

	public PerkName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PerkName perkName = (PerkName) o;
		return Objects.equals(name, perkName.name);
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
