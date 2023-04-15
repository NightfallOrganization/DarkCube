/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment;

public class PlayerStats {

	PlayerStatsType type;
	int menge;

	public PlayerStats(PlayerStatsType type, int menge) {
		this.type = type;
		this.menge = menge;
	}

	public static PlayerStats parseString(String s) {

		return new PlayerStats(PlayerStatsType.valueOf(s.split("``")[0]),
				Integer.parseInt(s.split("``")[1]));
	}

	public PlayerStatsType getType() {
		return type;
	}

	public void setType(PlayerStatsType type) {
		this.type = type;
	}

	public int getMenge() {
		return menge;
	}

	public void setMenge(int menge) {
		this.menge = menge;
	}

	@Override
	public String toString() {

		return type + "``" + menge;
	}
}
