/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment;

public enum ComponentTypes {
	AXE(new PlayerStats[] {new PlayerStats(PlayerStatsType.STRENGHT, 3)});

	private PlayerStats[] stats;

	ComponentTypes(PlayerStats[] stats) {
		this.stats = stats;
	}

	public PlayerStats[] getStats() {
		return stats;
	}
}

