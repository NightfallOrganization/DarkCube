/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment;

public enum ComponentType {
	AXE(new PlayerStats[] {new PlayerStats(PlayerStatsType.STRENGHT, 3)}),




	;

	private PlayerStats[] stats;

	ComponentType(PlayerStats[] stats) {
		this.stats = stats;
	}

	public PlayerStats[] getStats() {
		return stats;
	}
}

